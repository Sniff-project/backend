package com.sniff.user.controller;

import com.sniff.auth.model.AuthResponse;
import com.sniff.user.model.request.PasswordUpdate;
import com.sniff.user.model.request.UserUpdate;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User APIs documentation")
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class UserController {
    private final UserService userService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get user profile",
            description = "As a user, I want to be able to view my profile information, " +
                    "as well as the profile information of other users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                                    schema = @Schema(oneOf = {
                                            UserProfile.class,
                                            UserFullProfile.class
                                    }))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfile getUserProfile(@PathVariable Long id) {
        return userService.getUserProfileById(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update user profile",
            description = "As a user, I want to edit my profile information to keep it up to date"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = UserFullProfile.class),
                            mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Invalid fields",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists with this email/phone number",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserFullProfile updateUserProfile(@PathVariable Long id,
                                         @Valid @RequestBody UserUpdate updatedUser) {
        return userService.updateUserProfile(id, updatedUser);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change user password",
            description = "As a user, I want to be able to change my password to protect my account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid current password " +
                    "or new password is the same as the current password"),
    })
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@PathVariable Long id,
                               @Valid @RequestBody PasswordUpdate passwordUpdate) {
        userService.changePassword(id, passwordUpdate);
    }
}
