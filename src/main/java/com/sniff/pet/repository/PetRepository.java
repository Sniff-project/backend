package com.sniff.pet.repository;

import com.sniff.pet.model.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @Query("SELECT p.photos FROM pet p WHERE p.id = :id")
    List<String> findPhotosById(Long id);
}
