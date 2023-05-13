package com.sniff.mapper;

import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface Mappers {
    User toUser(UserSignUp userSignup);

    Pet toPet(PetProfileModify petProfileModify);

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserProfileWithoutPetProfiles")
    PetProfile toPetProfileWithUserProfile(Pet pet);

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserFullProfileWithoutPetProfiles")
    PetProfile toPetProfileWithUserFullProfile(Pet pet);

    default PetProfile toPetProfileWithoutUserProfile(Pet pet) {
        return PetProfile.builder()
                .id(pet.getId())
                .status(pet.getStatus())
                .photos(pet.getPhotos())
                .name(pet.getName())
                .latitude(pet.getLatitude())
                .longitude(pet.getLongitude())
                .gender(pet.getGender())
                .foundOrLostDate(pet.getFoundOrLostDate())
                .description(pet.getDescription())
                .build();
    }

    @Named("toUserProfileWithoutPetProfiles")
    default UserProfile toUserProfileWithoutPetProfiles(User user) {
        return UserProfile.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .region(user.getRegion())
                .city(user.getCity())
                .build();
    }

    default UserProfile toUserProfile(User user) {
        UserProfile userProfile = toUserProfileWithoutPetProfiles(user);
        userProfile.setPetProfiles(user.getPets().stream()
                        .map(this::toPetProfileWithoutUserProfile)
                        .collect(Collectors.toList()));
        return userProfile;
    }

    @Named("toUserFullProfileWithoutPetProfiles")
    default UserFullProfile toUserFullProfileWithoutPetProfiles(User user) {
        UserFullProfile userFullProfile = new UserFullProfile();
        userFullProfile.setId(user.getId());
        userFullProfile.setAvatar(user.getAvatar());
        userFullProfile.setFirstname(user.getFirstname());
        userFullProfile.setLastname(user.getLastname());
        userFullProfile.setRegion(user.getRegion());
        userFullProfile.setCity(user.getCity());
        userFullProfile.setEmail(user.getEmail());
        userFullProfile.setPhone(user.getPhone());
        return userFullProfile;
    }

    default UserFullProfile toUserFullProfile(User user) {
        UserFullProfile userFullProfile = toUserFullProfileWithoutPetProfiles(user);
        userFullProfile.setPetProfiles(user.getPets().stream()
                .map(this::toPetProfileWithoutUserProfile)
                .collect(Collectors.toList()));
        return userFullProfile;
    }
}
