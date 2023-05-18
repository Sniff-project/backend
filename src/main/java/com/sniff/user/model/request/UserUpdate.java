package com.sniff.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUpdate {
    @NotBlank(message = "Firstname shouldn't be blank")
    @Size(min = 2, max = 30, message = "Firstname should be between 2 and 30 characters")
    private String firstname;

    @NotBlank(message = "Lastname shouldn't be blank")
    @Size(min = 2, max = 30, message = "Lastname should be between 2 and 30 characters")
    private String lastname;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email shouldn't be blank")
    @Size(min = 5, max = 50, message = "Email should be between 5 and 50 characters")
    private String email;

    @NotBlank(message = "Phone shouldn't be blank")
    @Size(min = 10, max = 15, message = "Phone should be between 10 and 15 characters")
    private String phone;

    private Long regionId;

    private Long cityId;
}
