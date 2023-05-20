package com.sniff.pet.model.request;

import com.sniff.pet.enums.Gender;
import com.sniff.pet.enums.PetStatus;
import com.sniff.utils.enums.ValidEnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PetProfileModify {
    @ValidEnumValue(enumClass = PetStatus.class)
    private String status;

    @NotBlank(message = "Name should not be empty")
    private String name;

    @NotBlank(message = "Latitude should not be empty")
    private String latitude;

    @NotBlank(message = "Longitude should not be empty")
    private String longitude;

    @ValidEnumValue(enumClass = Gender.class)
    private String gender;

    @NotNull(message = "Date should not be empty")
    private LocalDate foundOrLostDate;

    private String description;
}
