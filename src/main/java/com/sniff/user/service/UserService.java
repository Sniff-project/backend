package com.sniff.user.service;

import com.sniff.auth.service.AuthVerifyService;
import com.sniff.mapper.UserMapper;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.user.model.entity.User;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
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

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
