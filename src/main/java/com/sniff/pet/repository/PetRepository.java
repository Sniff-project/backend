package com.sniff.pet.repository;

import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findAllByStatus(Pageable pageRequest, PetStatus status);
}
