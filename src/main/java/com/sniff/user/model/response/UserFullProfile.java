package com.sniff.user.model.response;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFullProfile extends UserProfile {
    private String email;

    private String phone;

    public UserFullProfile(String avatar,
                           String firstname,
                           String lastname,
                           String region,
                           String city,
                           String email,
                           String phone) {
        super(avatar, firstname, lastname, region, city);
        this.email = email;
        this.phone = phone;
    }
}
