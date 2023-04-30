package com.sniff.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.auth.controller.AuthController;
import com.sniff.auth.model.AuthResponse;
import com.sniff.auth.service.AuthService;
import com.sniff.jwt.JwtService;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignIn;
import com.sniff.user.model.request.UserSignUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWebMvc
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtService jwtService;
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
    @DisplayName("[Sprint-1] Sign up successfully")
    public void signUpSuccessfully() throws Exception {
        UserSignUp userSignup = generateSignupRequest();
        AuthResponse authResponse = new AuthResponse("token");

        given(jwtService.generateToken(anyLong(),anyString(), any())).willReturn("token");
        given(authService.signUp(any())).willReturn(authResponse);

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignup)));

        response
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").value("token"));
    }

    @Test
    @DisplayName("[Sprint-1] Sign up with early occupied email")
    public void signUpWithEarlyOccupiedEmail() throws Exception {
        UserSignUp userSignup = generateSignupRequest();

        given(authService.signUp(any()))
                .willThrow(new UserExistsException("User with this email already exists"));

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignup)));

        response
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign up without email")
    public void signUpWithoutEmail() throws Exception {
        UserSignUp userSignup = generateSignupRequest();
        userSignup.setEmail(null);

        given(authService.signUp(any())).willThrow(NullPointerException.class);

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignup)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign up with invalid phone")
    public void signUpWithInvalidPhone() throws Exception {
        UserSignUp userSignup = generateSignupRequest();
        userSignup.setPhone("380111111111111");

        given(authService.signUp(any())).willThrow(new InvalidPhoneException("Invalid phone number"));

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignup)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("[Sprint-1] Sign in successfully")
    public void signInSuccessfully() throws Exception {
        AuthResponse authResponse = new AuthResponse("token");

        given(jwtService.generateToken(anyLong(), anyString(), any())).willReturn("token");
        given(authService.signIn(any())).willReturn(authResponse);

        UserSignIn userSignin = new UserSignIn(user.getEmail(), "qwerty123456789");

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignin)));

        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").value("token"));
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign in with invalid password")
    public void signInWithInvalidPassword() throws Exception {
        UserSignIn userSignin = new UserSignIn(user.getEmail(), "another_password");

        given(authService.signIn(any())).willThrow(new BadCredentialsException("Invalid password"));

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignin)));

        response
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign in with invalid email")
    public void signInWithInvalidEmail() throws Exception {
        UserSignIn userSignin = new UserSignIn("another_email@gmail.com", "qwerty123456789");

        given(authService.signIn(any()))
                .willThrow(new UserNotFoundException("User with this email not found"));

        ResultActions response = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSignin)));

        response
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    private UserSignUp generateSignupRequest() {
        return UserSignUp.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password("qwerty123456789")
                .build();
    }
}
