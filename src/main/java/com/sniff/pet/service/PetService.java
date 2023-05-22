package com.sniff.pet.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.Mappers;
import com.sniff.pagination.PageWithMetadata;
import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.exceptions.PetNotBelongingToUserException;
import com.sniff.pet.exceptions.PetNotFoundException;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetCard;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.repository.PetRepository;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final AuthVerifyService authVerifyService;
    private final Mappers mapper;

    public PageWithMetadata<PetCard> getPetsGallery(int page, int size, PetStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pet> pets = (status == null)
                ? petRepository.findAll(pageable)
                : petRepository.findAllByStatus(pageable, status);
        List<PetCard> petCards = mapper.toPetCards(pets.getContent());
        return new PageWithMetadata<>(petCards, pets.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PetProfile getPetProfileById(Long id) {
        Pet pet = getPetById(id);
        return authVerifyService.isAuthenticated()
                ? mapper.toPetProfileWithUserFullProfile(pet)
                : mapper.toPetProfileWithUserProfile(pet);
    }

    public PetProfile createPetProfile(PetProfileModify petProfileModify) {
        User user = getUserById(authVerifyService.getIdFromSubject());
        Pet pet = mapper.toPet(petProfileModify);
        pet.setAuthor(user);
        petRepository.save(pet);
        user.getPets().add(pet);
        userRepository.save(user);
        return mapper.toPetProfileWithUserFullProfile(pet);
    }

    public PetProfile updatePetProfile(Long id, PetProfileModify updatedPet) {
        Pet petToUpdate = getPetById(id);
        User user = getUserById(authVerifyService.getIdFromSubject());
        verifyUserContainsPetProfile(user, petToUpdate);

        petToUpdate.setStatus(PetStatus.valueOf(updatedPet.getStatus()));
        petToUpdate.setName(updatedPet.getName());
        petToUpdate.setLatitude(updatedPet.getLatitude());
        petToUpdate.setLongitude(updatedPet.getLongitude());
        petToUpdate.setGender(Gender.valueOf(updatedPet.getGender()));
        petToUpdate.setFoundOrLostDate(updatedPet.getFoundOrLostDate());
        Optional.ofNullable(updatedPet.getDescription())
                .ifPresent(petToUpdate::setDescription);

        petRepository.save(petToUpdate);
        return mapper.toPetProfileWithUserFullProfile(petToUpdate);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));
    }

    private void verifyUserContainsPetProfile(User user, Pet pet) {
        if(!user.getPets().contains(pet)){
            throw new PetNotBelongingToUserException("You can't edit this pet profile");
        }
    }
}
