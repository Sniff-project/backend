package com.sniff.pet.model.request;

import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PetProfileModify {
    @NotNull(message = "Status should not be empty")
    private PetStatus status;

    @NotBlank(message = "Name should not be empty")
    private String name;

    @NotBlank(message = "Latitude should not be empty")
    private String latitude;

    @NotBlank(message = "Longitude should not be empty")
    private String longitude;

    @NotNull(message = "Gender should not be empty")
    private Gender gender;

    @NotNull(message = "Date should not be empty")
    private LocalDate foundOrLostDate;

    private String description;
}
