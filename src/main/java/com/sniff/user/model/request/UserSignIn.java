package com.sniff.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserSignIn {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email shouldn't be blank")
    @Size(min = 5, max = 50, message = "Email should be between 5 and 50 characters")
    private String email;

    @NotBlank(message = "Password shouldn't be blank")
    @Size(min = 6, max = 20, message = "Password should be between 6 and 20 characters")
    private String password;
}
