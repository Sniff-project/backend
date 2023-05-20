package com.sniff.auth.service;

import com.sniff.auth.model.AuthResponse;
import com.sniff.auth.role.Role;
import com.sniff.jwt.JwtService;
import com.sniff.mapper.Mappers;
import com.sniff.user.exception.InvalidPasswordException;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignIn;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.sniff.utils.Validation.isValidPassword;
import static com.sniff.utils.Validation.isValidPhone;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final Mappers mapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signUp(UserSignUp userSignUp) {
        String email = userSignUp.getEmail();
        String phone = userSignUp.getPhone();
        String password = userSignUp.getPassword();

        if(userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserExistsException("User with email " + email + " already exists");
        }

        if(!isValidPhone(phone)) {
            throw new InvalidPhoneException("Invalid phone number");
        }

        if(!isValidPassword(password)) {
            throw new InvalidPasswordException("The password must contain: A-z, 0-9, ! @ # $ % ^ & *() ?.");
        }

        if(userRepository.existsByPhone(phone)) {
            throw new UserExistsException("User with phone " + phone + " already exists");
        }

        User user = mapper.toUser(userSignUp);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getId(), user.getFirstname(), user.getRole());
        return new AuthResponse(jwtToken);
    }

    public AuthResponse signIn(UserSignIn userSignIn) {
        User user = userRepository.findByEmailIgnoreCase(userSignIn.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email " + userSignIn.getEmail() + " not found"));

        if(!passwordEncoder.matches(userSignIn.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String jwtToken = jwtService.generateToken(user.getId(), user.getFirstname(), user.getRole());
        return new AuthResponse(jwtToken);
    }
}
