package com.sniff.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.auth.service.AuthVerifyService;
import com.sniff.user.controller.UserController;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.service.UserService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWebMvc
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private AuthVerifyService authVerifyService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .build();
    }

    @Test
    @DisplayName("[Sprint-1] Get user profile when non-authenticated")
    public void getUserProfileWhenNonAuthenticated() throws Exception {
        UserProfile userProfile = new UserProfile("avatar",
                "Mark", "Himonov", "region", "city");
        given(userService.getUserProfileById(user.getId())).willReturn(userProfile);
        given(authVerifyService.isAuthenticated()).willReturn(false);

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON));

        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").doesNotExist());
    }

    @Test
    @DisplayName("[Sprint-1] Get user profile when authenticated")
    public void getUserProfileWhenAuthenticated() throws Exception {
        UserFullProfile userFullProfile = new UserFullProfile("avatar",
                "Mark", "Himonov", "region", "city", "email", "phone");
        given(userService.getUserProfileById(user.getId())).willReturn(userFullProfile);
        given(authVerifyService.isAuthenticated()).willReturn(true);

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON));

        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("[Sprint-1] Try to get user non-existent user profile")
    public void getUserProfileWhenNonExistentUser() throws Exception {
        given(userService.getUserProfileById(anyLong()))
                .willThrow(new UserNotFoundException("User not found"));

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON));

        response
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
