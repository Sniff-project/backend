package com.sniff.location.controller;

import com.sniff.location.model.response.Location;
import com.sniff.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
@Tag(name = "Location", description = "Location APIs documentation")
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class LocationController {
    private final LocationService locationService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all regions")
    @ApiResponse(responseCode = "200",
            content = { @Content(schema = @Schema(implementation = Location.class)) })
    @GetMapping("/regions")
    @ResponseStatus(HttpStatus.OK)
    public List<Location> getRegions() {
        return locationService.getRegions();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all cities")
    @ApiResponse(responseCode = "200",
            content = { @Content(schema = @Schema(implementation = Location.class)) })
    @GetMapping("/cities")
    @ResponseStatus(HttpStatus.OK)
    public List<Location> getCities() {
        return locationService.getCities();
    }
}
