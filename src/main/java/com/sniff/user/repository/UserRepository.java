package com.sniff.user.repository;

import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.entity.Pet;
import com.sniff.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmailIgnoreCase(String email);

    Boolean existsByPhone(String phone);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT p FROM pet p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND p.author.id = :id " +
            "ORDER BY p.id DESC")
    Page<Pet> getUserPetCards(Long id, PetStatus status, Pageable pageable);
}
