package com.sniff.auth;

import com.sniff.auth.model.AuthResponse;
import com.sniff.auth.service.AuthService;
import com.sniff.jwt.JwtService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignIn;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

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
    public void signUpSuccessfully() {
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(anyLong(), any())).thenReturn("token");

        UserSignUp userSignup = generateSignupRequest();
        when(userMapper.toUser(userSignup)).thenReturn(user);

        AuthResponse authResponse = authService.signUp(userSignup);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getJwtToken()).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-1] Sign up with early occupied email")
    public void signUpWithEarlyOccupiedEmail() {
        when(userRepository.save(any()))
                .thenThrow(new UserExistsException("User with this email already exists"));

        UserSignUp userSignup = generateSignupRequest();
        when(userMapper.toUser(userSignup)).thenReturn(user);

        assertThrows(UserExistsException.class, () -> authService.signUp(userSignup));
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign up without email")
    public void trySignUpWithoutEmail() {
        UserSignUp userSignup = generateSignupRequest();
        userSignup.setEmail(null);

        assertThrows(NullPointerException.class, () -> authService.signUp(userSignup));
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign up with invalid phone")
    public void trySignUpWithInvalidPhone() {
        UserSignUp userSignup = generateSignupRequest();
        userSignup.setPhone("380111111111111");

        assertThrows(InvalidPhoneException.class, () -> authService.signUp(userSignup));
    }

    @Test
    @DisplayName("[Sprint-1] Sign in successfully")
    public void signInSuccessfully() {
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyLong(), any())).thenReturn("token");

        UserSignIn userSignin = new UserSignIn(user.getEmail(), "qwerty123456789");
        when(passwordEncoder.matches(userSignin.getPassword(), user.getPassword())).thenReturn(true);


        AuthResponse authResponse = authService.signIn(userSignin);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getJwtToken()).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign in with invalid password")
    public void trySignInWithInvalidPassword() {
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));

        UserSignIn userSignin = new UserSignIn(user.getEmail(), "another_password");
        when(passwordEncoder.matches(userSignin.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.signIn(userSignin));
    }

    @Test
    @DisplayName("[Sprint-1] Try to sign in with invalid email")
    public void trySignInWithInvalidEmail() {
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());

        UserSignIn userSignin = new UserSignIn("another_email@gmail.com", "qwerty123456789");

        assertThrows(UserNotFoundException.class, () -> authService.signIn(userSignin));
    }

    private UserSignUp generateSignupRequest() {
        return UserSignUp.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .build();
    }
}
