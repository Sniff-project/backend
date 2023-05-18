package com.sniff.location.model.entity;

import com.sniff.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "region")
@Table(name = "region")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @OneToMany(mappedBy = "region",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<User> users;
}
