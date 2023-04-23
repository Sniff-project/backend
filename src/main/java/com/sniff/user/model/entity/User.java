package com.sniff.user.model.entity;

import com.sniff.auth.role.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avatar")
    private String avatar;

    @Column(nullable = false, length = 30, name = "firstname")
    private String firstname;

    @Column(nullable = false, length = 30, name = "lastname")
    private String lastname;

    @Column(unique = true, nullable = false, length = 50, name = "email")
    private String email;

    @Column(nullable = false, length = 15, name = "phone")
    private String phone;

    @Column(length = 50, name = "region")
    private String region;

    @Column(length = 50, name = "city")
    private String city;

    @Column(nullable = false, name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Role role;
}
