package com.sniff.pet.controller;

import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.service.PetService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pet", description = "Pet APIs documentation")
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class PetController {
    private final PetService petService;

    @Operation(
            summary = "Get pet profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = PetProfile.class)) }),
            @ApiResponse(responseCode = "404", description = "Pet not found",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetProfile getPetProfile(@PathVariable Long id) {
        return petService.getPetProfileById(id);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create pet profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = { @Content(schema = @Schema(implementation = PetProfile.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid fields",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetProfile createPetProfile(@Valid @RequestBody PetProfileModify petProfileModify) {
        return petService.createPetProfile(petProfileModify);
    }
}
