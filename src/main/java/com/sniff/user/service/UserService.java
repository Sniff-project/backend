package com.sniff.user.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.location.exception.CityNotFoundException;
import com.sniff.location.exception.RegionNotFoundException;
import com.sniff.mapper.Mappers;
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

        if(updatedUser.getRegionId() != null){
            Region region = getRegionById(updatedUser.getRegionId());
            userToUpdate.setRegion(region);
            region.getUsers().add(userToUpdate);
        }
        if(updatedUser.getCityId() != null){
            City city = getCityById(updatedUser.getCityId());
            userToUpdate.setCity(city);
            city.getUsers().add(userToUpdate);
        }

        userRepository.save(userToUpdate);

        return mappers.toUserFullProfile(userToUpdate);
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

    private Region getRegionById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException("Region not found"));
    }

    private City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found"));
    }
}
