package com.sniff.user.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.location.exception.CityNotFoundException;
import com.sniff.location.exception.RegionNotFoundException;
import com.sniff.mapper.Mappers;
import com.sniff.user.exception.InvalidPasswordException;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.location.model.entity.City;
import com.sniff.location.model.entity.Region;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.PasswordUpdate;
import com.sniff.user.model.request.UserUpdate;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.location.repository.CityRepository;
import com.sniff.location.repository.RegionRepository;
import com.sniff.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sniff.utils.Validation.isValidPassword;
import static com.sniff.utils.Validation.isValidPhone;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mappers mappers;
    private final AuthVerifyService authVerifyService;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfile getUserProfileById(Long id) {
        User user = getUserById(id);
        return authVerifyService.isAuthenticated()
                ? mappers.toUserFullProfile(user)
                : mappers.toUserProfile(user);
    }

    public UserFullProfile updateUserProfile(Long id, UserUpdate updatedUser) {
        User userToUpdate = getUserById(id);
        authVerifyService.verifyAccess(id);

        String email = updatedUser.getEmail();
        String phone = updatedUser.getPhone();

        if (!userToUpdate.getEmail().equals(email)) {
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new UserExistsException("User with email " + email + " already exists");
            }
            userToUpdate.setEmail(email);
        }

        if (!userToUpdate.getPhone().equals(phone)) {
            if (!isValidPhone(phone)) {
                throw new InvalidPhoneException("Invalid phone number");
            }

            if (userRepository.existsByPhone(phone)) {
                throw new UserExistsException("User with phone " + phone + " already exists");
            }
            userToUpdate.setPhone(phone);
        }

        userToUpdate.setFirstname(updatedUser.getFirstname());
        userToUpdate.setLastname(updatedUser.getLastname());

        Optional<Region> optionalRegion = Optional.ofNullable(updatedUser.getRegionId())
                .map(this::getRegionById);
        userToUpdate.setRegion(optionalRegion.orElse(null));

        Optional<City> optionalCity = Optional.ofNullable(updatedUser.getCityId())
                .map(this::getCityById);
        userToUpdate.setCity(optionalCity.orElse(null));

        optionalRegion.ifPresent(region -> region.getUsers().add(userToUpdate));
        optionalCity.ifPresent(city -> city.getUsers().add(userToUpdate));


        userRepository.save(userToUpdate);

        return mappers.toUserFullProfile(userToUpdate);
    }

    public void changePassword(Long id, PasswordUpdate passwordUpdate) {
        User user = getUserById(id);
        authVerifyService.verifyAccess(id);

        String currentPassword = passwordUpdate.getCurrentPassword();
        String newPassword = passwordUpdate.getNewPassword();
        String hashedPassword = user.getPassword();

        if(!isValidPassword(currentPassword) || !isValidPassword(newPassword)) {
            throw new InvalidPasswordException("The password must contain: A-z, 0-9, ! @ # $ % ^ & *() ?.");
        }

        if(!passwordEncoder.matches(currentPassword, hashedPassword)) {
            throw new BadCredentialsException("Invalid current password");
        }

        if(passwordEncoder.matches(newPassword, hashedPassword)) {
            throw new BadCredentialsException("New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Region getRegionById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException("Region not found"));
    }

    private City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found"));
    }
}
