package com.sniff.pet.model.entity;

import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import com.sniff.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@Entity(name = "pet")
@Table(name = "pet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PetStatus status;

    @ElementCollection(fetch = EAGER)
    @Column(name = "photos")
    private List<String> photos;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "longitude", nullable = false)
    private String longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "found_or_lost_date", nullable = false)
    private LocalDate foundOrLostDate;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
}
