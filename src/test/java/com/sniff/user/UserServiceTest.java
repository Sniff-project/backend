package com.sniff.user;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.repository.UserRepository;
import com.sniff.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthVerifyService authVerifyService;

    @InjectMocks
    private UserService userService;

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
    public void getUserProfileWhenNonAuthenticated() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(authVerifyService.isAuthenticated()).willReturn(false);

        when(userMapper.toUserProfile(any())).thenReturn(new UserProfile());

        UserProfile userProfile = userService.getUserProfileById(user.getId());

        assertThat(userProfile).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-1] Get user profile when authenticated")
    public void getUserProfileWhenAuthenticated() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(authVerifyService.isAuthenticated()).willReturn(true);

        when(userMapper.toUserFullProfile(any())).thenReturn(new UserFullProfile());

        UserFullProfile userfullProfile = (UserFullProfile) userService.getUserProfileById(user.getId());

        assertThat(userfullProfile).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-1] Try to get user non-existent user profile")
    public void getUserProfileWhenNonExistentUser() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileById(user.getId()));
    }

}
