package com.sniff.pet.model.response;

import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import com.sniff.user.model.response.UserProfile;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetProfile {
    private Long id;

    private PetStatus status;

    private List<String> photos;

    private String name;

    private String latitude;

    private String longitude;

    private Gender gender;

    private LocalDate foundOrLostDate;

    private String description;

    private UserProfile author;
}
