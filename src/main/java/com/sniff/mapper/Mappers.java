package com.sniff.mapper;

import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetCard;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface Mappers {
    User toUser(UserSignUp userSignup);

    default Pet toPet(PetProfileModify petProfileModify){
        return Pet.builder()
                .status(PetStatus.valueOf(petProfileModify.getStatus()))
                .name(petProfileModify.getName())
                .latitude(petProfileModify.getLatitude())
                .longitude(petProfileModify.getLongitude())
                .gender(Gender.valueOf(petProfileModify.getGender()))
                .foundOrLostDate(petProfileModify.getFoundOrLostDate())
                .description(petProfileModify.getDescription())
                .build();
    }

    default List<PetCard> toPetCards(List<Pet> pets) {
        return pets.stream()
                .map(this::toPetCard)
                .collect(Collectors.toList());
    }

    default PetCard toPetCard(Pet pet) {
        return PetCard.builder()
                .id(pet.getId())
                .photo(pet.getPhotos().stream().findFirst().orElse(null))
                .name(pet.getName())
                .build();
    }

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserProfile")
    PetProfile toPetProfileWithUserProfile(Pet pet);

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserFullProfile")
    PetProfile toPetProfileWithUserFullProfile(Pet pet);

    @Named("toUserProfile")
    default UserProfile toUserProfile(User user) {
        return UserProfile.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .region(user.getRegion() != null ? user.getRegion().getName() : null)
                .city(user.getCity() != null ? user.getCity().getName() : null)
                .build();
    }

    @Named("toUserFullProfile")
    default UserFullProfile toUserFullProfile(User user) {
        UserFullProfile userFullProfile = new UserFullProfile();
        userFullProfile.setId(user.getId());
        userFullProfile.setAvatar(user.getAvatar());
        userFullProfile.setFirstname(user.getFirstname());
        userFullProfile.setLastname(user.getLastname());
        userFullProfile.setRegion(user.getRegion() != null ? user.getRegion().getName() : null);
        userFullProfile.setCity(user.getCity() != null ? user.getCity().getName() : null);
        userFullProfile.setEmail(user.getEmail());
        userFullProfile.setPhone(user.getPhone());
        return userFullProfile;
    }
}

