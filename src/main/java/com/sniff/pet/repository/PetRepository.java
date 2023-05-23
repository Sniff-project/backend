package com.sniff.pet.repository;

import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @Query("SELECT p FROM pet p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND (:regionId IS NULL OR p.author.region.id = :regionId) " +
            "AND (:cityId IS NULL OR p.author.city.id = :cityId)")
    Page<Pet> findPetsByStatusRegionAndCity(PetStatus status,
                                            Long regionId,
                                            Long cityId,
                                            Pageable pageable);
}
