package com.sniff.user.controller;

import com.sniff.pagination.PageWithMetadata;
import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.response.PetCard;
import com.sniff.user.model.request.PasswordUpdate;
import com.sniff.user.model.request.UserUpdate;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import com.sniff.user.service.UserService;
import com.sniff.utils.HttpResponse;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
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
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfile getUserProfile(@PathVariable Long id) {
        return userService.getUserProfileById(id);
    }

    @Operation(summary = "Get user's pet cards")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = PetCard.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @GetMapping("/{id}/pets")
    @ResponseStatus(HttpStatus.OK)
    public PageWithMetadata<PetCard> getUserPetCards(
            @PathVariable Long id,
            @Min(value = 0, message = "Page should be greater or equals 0")
            @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size should be positive")
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) PetStatus status) {
        return userService.getUserPetCards(id, page, size, status);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update user profile",
            description = "As a user, I want to edit my profile information to keep it up to date"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = UserFullProfile.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid fields",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "409", description = "User already exists with this email/phone number",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
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
                    "or new password is the same as the current password",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
    })
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@PathVariable Long id,
                               @Valid @RequestBody PasswordUpdate passwordUpdate) {
        userService.changePassword(id, passwordUpdate);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
