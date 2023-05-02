package com.sniff.user.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.PasswordUpdate;
import com.sniff.user.model.request.UserUpdate;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sniff.utils.Validation.isValidPhone;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthVerifyService authVerifyService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfile getUserProfileById(Long id) {
        User user = getUserById(id);
        return authVerifyService.isAuthenticated()
                ? userMapper.toUserFullProfile(user)
                : userMapper.toUserProfile(user);
    }

    public UserFullProfile updateUserProfile(Long id, UserUpdate updatedUser) {
        User userToUpdate = getUserById(id);

        authVerifyService.verifyAccess(id);

        if (!userToUpdate.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(updatedUser.getEmail())) {
                throw new UserExistsException("User with email " + updatedUser.getEmail() + " already exists");
            }
            userToUpdate.setEmail(updatedUser.getEmail());
        }

        if (!userToUpdate.getPhone().equals(updatedUser.getPhone())) {
            if (!isValidPhone(updatedUser.getPhone())) {
                throw new InvalidPhoneException("Invalid phone number");
            }

            if (userRepository.existsByPhone(updatedUser.getPhone())) {
                throw new UserExistsException("User with phone " + updatedUser.getPhone() + " already exists");
            }
            userToUpdate.setPhone(updatedUser.getPhone());
        }

        userToUpdate.setFirstname(updatedUser.getFirstname());
        userToUpdate.setLastname(updatedUser.getLastname());

        Optional.ofNullable(updatedUser.getRegion()).ifPresent(userToUpdate::setRegion);
        Optional.ofNullable(updatedUser.getCity()).ifPresent(userToUpdate::setCity);

        userRepository.save(userToUpdate);

        return userMapper.toUserFullProfile(userToUpdate);
    }

    public void changePassword(Long id, PasswordUpdate passwordUpdate) {
        User user = getUserById(id);
        authVerifyService.verifyAccess(id);

        if(!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid current password");
        }

        if(passwordEncoder.matches(passwordUpdate.getNewPassword(), user.getPassword())) {
            throw new BadCredentialsException("New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
