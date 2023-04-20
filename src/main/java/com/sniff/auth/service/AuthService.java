package com.sniff.auth.service;

import com.sniff.auth.model.AuthResponse;
import com.sniff.auth.role.Role;
import com.sniff.jwt.JwtService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final KeyPair keyPair;

    public AuthResponse signUp(UserSignUp userSignUp) {
        if(userRepository.existsByEmailIgnoreCase(userSignUp.getEmail())) {
            throw new UserExistsException("User with email " + userSignUp.getEmail() + " already exists");
        }
        if(!isValidPhone(userSignUp.getPhone())) {
            throw new InvalidPhoneException("Invalid phone number");
        }
        if(userRepository.existsByPhone(userSignUp.getPhone())) {
            throw new UserExistsException("User with phone " + userSignUp.getPhone() + " already exists");
        }

        User user = userMapper.toUser(userSignUp);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getId(), user.getRole());
        return new AuthResponse(jwtToken);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\+380\\d{9}$");
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
}