package com.sniff.filestore.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sniff.auth.service.AuthVerifyService;
import com.sniff.filestore.enums.FileStoreOperation;
import com.sniff.filestore.exception.FailedToUploadFileException;
import com.sniff.pet.exceptions.PetNotFoundException;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.repository.PetRepository;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.repository.UserRepository;
import com.sniff.utils.UrlModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sniff.filestore.enums.FileStoreOperation.PET;
import static com.sniff.filestore.enums.FileStoreOperation.USER;
import static com.sniff.utils.ImageUtils.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FileStoreService {
    @Value("${aws.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3;
    private final AuthVerifyService authVerifyService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final static int MAX_TOTAL_PET_FILES = 5;

    public List<String> uploadUserAvatar(Long id, MultipartFile image) {
        validateFile(image);
        authVerifyService.verifyAccess(id);
        User user = getUserById(id);

        String objectKey = generateObjectKey(id, USER);
        String imageName = getFileNameWithExtension(UUID.randomUUID().toString(), image.getOriginalFilename());

        try (InputStream inputStream = compressImage(image)) {
            deleteAllImagesByEntityId(id, USER);
            saveImage(objectKey, imageName, inputStream);
            user.setAvatar(generateUrl(id, imageName, USER));
            return Collections.singletonList(user.getAvatar());
        } catch (IOException e) {
            throw new FailedToUploadFileException(e.getMessage());
        }
    }

    public List<String> uploadPetPhotos(Long id, List<MultipartFile> images) {
        validateFiles(images);

        Pet pet = getPetById(id);
        User user = getUserById(authVerifyService.getIdFromSubject());
        verifyUserContainsPetProfile(user, pet);

        if(pet.getPhotos().size() + images.size() > MAX_TOTAL_PET_FILES) {
            throw new FailedToUploadFileException("You can't upload more than 5 photos");
        }

        String objectKey = generateObjectKey(id, PET);
        for(MultipartFile image : images){
            validateFile(image);
            String imageName = getFileNameWithExtension(UUID.randomUUID().toString(),
                    image.getOriginalFilename());
            try (InputStream inputStream = compressImage(image)) {
                saveImage(objectKey, imageName, inputStream);
                pet.getPhotos().add(generateUrl(id, imageName, PET));
            } catch (IOException e) {
                throw new FailedToUploadFileException(e.getMessage());
            }
        }
        return pet.getPhotos();
    }

    public void deleteImagesByUrlsAndPetId(Long id, UrlModel urlModel) {
        Pet pet = getPetById(id);
        List<String> photoUrls = pet.getPhotos();
        for (String url : urlModel.getUrls()) {
            if (photoUrls.contains(url)) {
                deleteImageIfPresent(Optional.of(url));
                photoUrls.remove(url);
            }
        }
        pet.setPhotos(photoUrls);
        petRepository.save(pet);
    }

    public void deleteAllImagesByEntityId(Long id, FileStoreOperation operation) {
        if (operation.equals(USER)) {
            User user = getUserById(id);
            deleteImageIfPresent(Optional.of(user.getAvatar()));
            user.setAvatar(null);
        } else if (operation.equals(PET)) {
            Pet pet = getPetById(id);
            deleteImagesIfPresent(pet.getPhotos());
            pet.getPhotos().clear();
        }
    }

    private void deleteImageIfPresent(Optional<String> imageUrl) {
        if(imageUrl.isPresent()){
            int prefixLength = "https://".length();
            String image = imageUrl.get();
            int bucketNameEndIndex = image.indexOf(".s3.amazonaws.com/");
            String bucketName = image.substring(prefixLength, bucketNameEndIndex);
            String key = image.substring(bucketNameEndIndex + ".s3.amazonaws.com/".length());
            s3.deleteObject(bucketName, key);
        }
    }

    private void deleteImagesIfPresent(List<String> imageUrls) {
        for(String imageUrl : imageUrls){
            deleteImageIfPresent(Optional.of(imageUrl));
        }
    }


    private void saveImage(String objectKey, String fileName, InputStream inputStream) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(inputStream.available());
        s3.putObject(objectKey, fileName, inputStream, metadata);
    }

    private void validateFile(MultipartFile image) {
        isFileEmpty(image);
        isImage(image);
    }

    private void validateFiles(List<MultipartFile> images) {
        for(MultipartFile image : images){
            isFileEmpty(image);
            isImage(image);
        }
    }

    private String generateObjectKey(Long id, FileStoreOperation operation) {
        return String.format("%s/%s/%s", bucketName, getGeneralFolderName(operation), id);
    }

    private String generateUrl(Long id, String fileName, FileStoreOperation operation) {
        return String.format("https://%s.s3.amazonaws.com/%s/%s/%s",
                bucketName, getGeneralFolderName(operation), id, fileName);
    }

    private String getGeneralFolderName(FileStoreOperation operation) {
        return operation.name().toLowerCase();
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void verifyUserContainsPetProfile(User user, Pet pet) {
        if(!user.getPets().contains(pet)){
            throw new UserNotFoundException("User not found");
        }
    }

    private Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));
    }


}
