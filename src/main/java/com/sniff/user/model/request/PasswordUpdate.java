package com.sniff.user.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordUpdate {
    @NotBlank(message = "Current password shouldn't be blank")
    @Size(min = 8, max = 20, message = "Current password should be between 8 and 20 characters")
    private String currentPassword;

    @NotBlank(message = "New password shouldn't be blank")
    @Size(min = 8, max = 20, message = "New password should be between 8 and 20 characters")
    private String newPassword;
}
