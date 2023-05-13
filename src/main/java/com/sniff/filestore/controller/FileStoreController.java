package com.sniff.filestore.controller;

import com.sniff.filestore.enums.FileStoreOperation;
import com.sniff.filestore.service.FileStoreService;
import com.sniff.utils.HttpResponse;
import com.sniff.utils.UrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/images")
@Tag(name = "Images", description = "Images APIs documentation")
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
@Validated
public class FileStoreController {
    private final FileStoreService fileStoreService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Image upload to user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded",
                    content = { @Content(schema = @Schema(implementation = UrlResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = { @Content(schema = @Schema(implementation = HttpResponse.class)) }) })
    @PostMapping(
            path = "users/{id}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public UrlResponse uploadUserAvatar(@PathVariable Long id,
                                        @Parameter(required = true, description = "Image file")
                                   @RequestParam
                                   MultipartFile image){
        return new UrlResponse(fileStoreService.uploadUserAvatar(id, image));
    }

    @PostMapping(
            path = "pets/{id}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public UrlResponse uploadPetProfilesPhotos(@PathVariable Long id,
                                        @Parameter(required = true, description = "Image file")
                                        @RequestParam
                                        List<MultipartFile> images){
        return new UrlResponse(fileStoreService.uploadPetPhotos(id, images));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImages(@PathVariable Long id,
                             @RequestBody FileStoreOperation operation) {
        fileStoreService.deleteImageByEntityId(id, operation);
    }
}