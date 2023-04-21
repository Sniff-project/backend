package com.sniff.user.repository;

import com.sniff.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmailIgnoreCase(String email);

    Boolean existsByPhone(String phone);

    Optional<User> findByEmailIgnoreCase(String email);
}
