package com.sniff.pet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.jwt.JwtService;
import com.sniff.pet.controller.PetController;
import com.sniff.pet.model.entity.Pet;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.service.PetService;
import com.sniff.user.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.sniff.pet.enums.Gender.MALE;
import static com.sniff.pet.enums.PetStatus.LOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWebMvc
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PetController.class)
public class PetControllerTest {
    @MockBean
    private PetService petService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void createPetProfileSuccessfully() throws Exception {
        PetProfile petProfile = PetProfile.builder()
                .id(pet.getId())
                .status(pet.getStatus())
                .name(pet.getName())
                .latitude(pet.getLatitude())
                .longitude(pet.getLongitude())
                .gender(pet.getGender())
                .foundOrLostDate(pet.getFoundOrLostDate())
                .description(pet.getDescription())
                .build();
        given(petService.createPetProfile(any(PetProfileModify.class))).willReturn(petProfile);

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generateModifyRequest()))
                        .accept(MediaType.APPLICATION_JSON));

        response
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists());
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
