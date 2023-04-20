package com.sniff.auth.controller;

import com.sniff.auth.model.AuthResponse;
import com.sniff.auth.service.AuthService;
import com.sniff.user.model.request.UserSignUp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth APIs documentation")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Sign up with an email and phone number",
            description = "As a guest, I would like to sign up with an email address and phone number " +
                    "so that I can use all the options on the site."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = { @Content(schema = @Schema(implementation = AuthResponse.class),
                            mediaType = "application/json") }),
            @ApiResponse(responseCode = "409", description = "User already exists with this email/phone number",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid phone number", content = @Content)
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@Valid @RequestBody UserSignUp userSignup) {
        return authService.signUp(userSignup);
    }

    @GetMapping("/public-key")
    public String getPublicKey() {
        return authService.getPublicKey();
    }
}
