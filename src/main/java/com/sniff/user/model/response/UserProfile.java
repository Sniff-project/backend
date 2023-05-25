package com.sniff.user.model.response;

import com.sniff.pet.model.response.PetCard;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class UserProfile {
    private Long id;

    private String avatar;

    private String firstname;

    private String lastname;

    private String region;

    private String city;

    private List<PetCard> petCards;
}
