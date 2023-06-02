package com.sniff.user.model.response;

import com.sniff.pet.model.response.PetCard;
import com.sniff.pet.model.response.PetProfile;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFullProfile extends UserProfile {
    private String email;

    private String phone;

    public UserFullProfile(Long id,
                           String avatar,
                           String firstname,
                           String lastname,
                           String region,
                           String city,
                           String email,
                           String phone) {
        super(id, avatar, firstname, lastname, region, city);
        this.email = email;
        this.phone = phone;
    }
}
