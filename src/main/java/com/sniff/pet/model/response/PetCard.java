package com.sniff.pet.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetCard {
    private Long id;
    private String photo;
    private String name;
}
