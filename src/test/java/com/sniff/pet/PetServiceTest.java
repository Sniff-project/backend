package com.sniff.pet;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.Mappers;
import com.sniff.pet.exceptions.PetNotBelongingToUserException;
import com.sniff.pet.exceptions.PetNotFoundException;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.repository.PetRepository;
import com.sniff.pet.service.PetService;
import com.sniff.user.model.entity.User;
import com.sniff.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static com.sniff.pet.enums.Gender.MALE;
import static com.sniff.pet.enums.PetStatus.LOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @Mock
    private PetRepository petRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Mappers mappers;
    @Mock
    private AuthVerifyService authVerifyService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PetService petService;

    private Pet pet;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .avatar("avatar")
                .firstname("Mark")
                .lastname("Himonov")
                .email("mark@gmail.com")
                .phone("+380111111111")
                .password(passwordEncoder.encode("qwerty123456789"))
                .pets(new ArrayList<>())
                .build();
        pet = Pet.builder()
                .id(1L)
                .status(LOST)
                .name("Pet")
                .latitude("latitude")
                .longitude("longitude")
                .gender(MALE)
                .foundOrLostDate(LocalDate.now())
                .description("Description")
                .author(user)
                .build();
    }

    @Test
    @DisplayName("[Sprint-3] Create pet profile successfully")
    public void createPetProfileSuccessfully() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(mappers.toPet(any())).willReturn(pet);
        given(mappers.toPetProfileWithUserFullProfile(any())).willReturn(new PetProfile());
        given(petRepository.save(any())).willReturn(pet);

        PetProfile petProfile = petService.createPetProfile(generateModifyRequest());

        assertThat(petProfile).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-3] Update own pet profile successfully")
    public void updateOwnPetProfileSuccessfully() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(petRepository.findById(anyLong())).willReturn(Optional.of(pet));
        user.getPets().add(pet);
        when(mappers.toPetProfileWithUserFullProfile(any())).thenReturn(new PetProfile());

        PetProfileModify petProfileModify = generateModifyRequest();

        PetProfile petProfile = petService.updatePetProfile(pet.getId(), petProfileModify);

        assertThat(petProfile).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-3] Try update someone else pet profile")
    public void tryUpdateSomeoneElsePetProfile() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(petRepository.findById(anyLong())).willReturn(Optional.of(pet));
        user.getPets().clear();
        pet.setAuthor(null);
        assertThrows(PetNotBelongingToUserException.class,
                () -> petService.updatePetProfile(pet.getId(), generateModifyRequest()));
    }

    @Test
    @DisplayName("[Sprint-3] Try to update non-existent pet profile")
    public void tryUpdateNonExistentPetProfile() {
        given(petRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(PetNotFoundException.class,
                () -> petService.updatePetProfile(pet.getId(), generateModifyRequest()));
    }

    private PetProfileModify generateModifyRequest() {
        return PetProfileModify.builder()
                .status(LOST)
                .name("Pet")
                .latitude("latitude")
                .longitude("longitude")
                .gender(MALE)
                .foundOrLostDate(LocalDate.now())
                .description("Description")
                .build();
    }

}
