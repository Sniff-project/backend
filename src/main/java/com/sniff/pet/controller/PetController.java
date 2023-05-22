package com.sniff.pet.controller;

import com.sniff.pagination.PageWithMetadata;
import com.sniff.pet.enums.PetStatus;
import com.sniff.pet.model.request.PetProfileModify;
import com.sniff.pet.model.response.PetCard;
import com.sniff.pet.model.response.PetProfile;
import com.sniff.pet.service.PetService;
import com.sniff.utils.HttpResponse;
import com.sniff.utils.enums.ValidEnumValue;
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
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Validated
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

    @Operation(summary = "Get pet gallery")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = PetCard.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid query params",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageWithMetadata<PetCard> getPetsGallery(
            @Min(value = 0, message = "Page should be greater or equals 0")
            @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size should be positive")
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false)
            PetStatus status) {
        return petService.getPetsGallery(page, size, status);
    }

    @Operation(summary = "Get pet profile")
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
    @Operation(summary = "Create pet profile")
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

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update pet profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = { @Content(schema = @Schema(implementation = PetProfile.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid fields",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Pet not found",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) })
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetProfile updatePetProfile(@PathVariable Long id,
                                       @Valid @RequestBody PetProfileModify updatedPet) {
        return petService.updatePetProfile(id, updatedPet);
    }
}
