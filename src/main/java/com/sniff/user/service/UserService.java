package com.sniff.user.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserUpdate;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public UserProfile getUserProfileById(Long id) {
        User user = getUserById(id);
        return authVerifyService.isAuthenticated()
                ? userMapper.toUserFullProfile(user)
                : userMapper.toUserProfile(user);
    }

    public UserProfile updateUserProfile(Long id, UserUpdate updatedUser) {
        User userToUpdate = getUserById(id);

        authVerifyService.verifyAccess(id);

        if(!userToUpdate.getEmail().equals(updatedUser.getEmail())) {
            if(userRepository.existsByEmailIgnoreCase(updatedUser.getEmail())) {
                throw new UserExistsException("User with email " + updatedUser.getEmail() + " already exists");
            }
            userToUpdate.setEmail(updatedUser.getEmail());
        }

        if(!userToUpdate.getPhone().equals(updatedUser.getPhone())) {
            if(!isValidPhone(updatedUser.getPhone())) {
                throw new InvalidPhoneException("Invalid phone number");
            }

            if(userRepository.existsByPhone(updatedUser.getPhone())) {
                throw new UserExistsException("User with phone " + updatedUser.getPhone() + " already exists");
            }
            userToUpdate.setPhone(updatedUser.getPhone());
        }

        userToUpdate.setFirstname(updatedUser.getFirstname());
        userToUpdate.setLastname(updatedUser.getLastname());

        Optional<String> optionalRegion = Optional.ofNullable(updatedUser.getRegion());
        optionalRegion.ifPresent(userToUpdate::setRegion);

        Optional<String> optionalCity = Optional.ofNullable(updatedUser.getCity());
        optionalCity.ifPresent(userToUpdate::setCity);

        userRepository.save(userToUpdate);

        return userMapper.toUserFullProfile(userToUpdate);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
