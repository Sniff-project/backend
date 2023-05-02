package com.sniff.user;

import com.sniff.auth.exception.DeniedAccessException;
import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.PasswordUpdate;
import com.sniff.user.model.request.UserUpdate;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willThrow;
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

    @Test
    @DisplayName("[Sprint-2] Update own profile successfully")
    public void editOwnProfileSuccessfully() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        when(userMapper.toUserFullProfile(any())).thenReturn(new UserFullProfile());

        UserUpdate userUpdate = generateUpdateRequest();

        UserProfile userProfile = userService.updateUserProfile(user.getId(), userUpdate);

        assertThat(userProfile).isNotNull();
    }

    @Test
    @DisplayName("[Sprint-2] Try update someone else profile")
    public void editSomeoneElseProfile() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        willThrow(DeniedAccessException.class).given(authVerifyService).verifyAccess(user.getId());

        assertThrows(DeniedAccessException.class,
                () -> userService.updateUserProfile(user.getId(), generateUpdateRequest()));
    }

    @Test
    @DisplayName("[Sprint-2] Try to update non-existent user profile")
    public void editNonExistentUserProfile() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(user.getId(), generateUpdateRequest()));
    }

    @Test
    @DisplayName("[Sprint-2] Change password successfully")
    public void changePasswordSuccessfully() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        PasswordUpdate passwordUpdate = generatePasswordUpdateRequest();

        when(passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword()))
                .thenReturn(true);

        userService.changePassword(user.getId(), passwordUpdate);

        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode("newPassword"));
    }

    @Test
    @DisplayName("[Sprint-2] Try to change password with wrong current password")
    public void changePasswordWithWrongCurrentPassword() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        PasswordUpdate passwordUpdate = generatePasswordUpdateRequest();

        when(passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.changePassword(user.getId(), passwordUpdate));
    }

    @Test
    @DisplayName("[Sprint-2] Try to change password with same current password")
    public void changePasswordWithSameCurrentPassword() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        PasswordUpdate passwordUpdate = generatePasswordUpdateRequest();

        when(passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword()))
                .thenReturn(true);

        when(passwordEncoder.matches(passwordUpdate.getNewPassword(), user.getPassword()))
                .thenReturn(true);

        assertThrows(BadCredentialsException.class,
                () -> userService.changePassword(user.getId(), passwordUpdate));
    }

    @Test
    @DisplayName("[Sprint-2] Try to change someone else's password")
    public void changeSomeoneElsePassword() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        PasswordUpdate passwordUpdate = generatePasswordUpdateRequest();

        willThrow(DeniedAccessException.class).given(authVerifyService).verifyAccess(user.getId());

        assertThrows(DeniedAccessException.class,
                () -> userService.changePassword(user.getId(), passwordUpdate));
    }

    @Test
    @DisplayName("[Sprint-2] Try to change non-existent user's password")
    public void changeNonExistentUserPassword() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(user.getId(), generatePasswordUpdateRequest()));
    }

    private UserUpdate generateUpdateRequest(){
        return UserUpdate.builder()
                .firstname("John")
                .lastname("Doe")
                .email("johndoe@example.com")
                .phone("+380111111111")
                .region("Ukraine")
                .city("Kiev")
                .build();
    }

    private PasswordUpdate generatePasswordUpdateRequest(){
        return new PasswordUpdate("qwerty123456789", "newPassword");
    }
}
