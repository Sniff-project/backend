package com.sniff.pet.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.Mappers;
import com.sniff.pet.exceptions.PetNotFoundException;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.repository.PetRepository;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final AuthVerifyService authVerifyService;
    private final Mappers mapper;

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

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));
    }
}
