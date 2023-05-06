package com.sniff.filestore.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sniff.auth.service.AuthVerifyService;
import com.sniff.filestore.exception.FailedToUploadFileException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

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

    public String uploadImage(Long id, MultipartFile image) {
        validateFile(image);
        authVerifyService.verifyAccess(id);
        User user = getUserById(id);

        String objectKey = generateObjectKey(id);
        String imageName = getFileNameWithExtension("avatar", image.getOriginalFilename());

        try (InputStream inputStream = compressImage(image)) {
            saveImage(objectKey, imageName, inputStream);
            user.setAvatar(generateUrl(id, imageName));
            return user.getAvatar();
        } catch (IOException e) {
            throw new FailedToUploadFileException(e.getMessage());
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

    private String generateObjectKey(Long id) {
        return String.format("%s/%s", bucketName, id);
    }

    private String generateUrl(Long id, String fileName) {
        return String.format("https://%s.s3.amazonaws.com/%s/%s", bucketName, id, fileName);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}

