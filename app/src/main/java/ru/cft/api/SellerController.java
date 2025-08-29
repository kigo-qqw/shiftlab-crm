package ru.cft.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.cft.dto.ErrorDetailsDto;
import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.service.SellerService;

import java.net.URI;
import java.util.List;

import static ru.cft.configuration.WebPath.API_VERSION_V1;
import static ru.cft.configuration.WebPath.PATH_SELLER;

@RestController
@Tag(name = "Sellers operations", description = "CRUD operations related to seller")
@RequestMapping(API_VERSION_V1 + PATH_SELLER)
@Validated
@Slf4j
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @GetMapping
    @Operation(summary = "Get all sellers", description = "Returns a list of all sellers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SellerDto.class))
                    )
            )
    })
    public ResponseEntity<List<SellerDto>> findAll() {
        log.trace("Fetching all sellers");
        var sellerDtos = this.sellerService.findAll();
        log.trace("sellerDtos={}", sellerDtos);
        return ResponseEntity.ok(sellerDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seller by ID", description = "Returns seller details for the given ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved seller",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seller not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Seller not found",
                                            value = "{ \"timestamp\": \"2025-08-27T12:34:56\", " +
                                                    "\"message\": \"There is no seller with id 42\", " +
                                                    "\"description\": \"uri=/api/v1/seller/42\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<SellerDto> findById(@PathVariable("id") final Long id) {
        log.trace("Fetching seller with id={}", id);
        var sellerDto = this.sellerService.findById(id);
        log.trace("sellerDto={}", sellerDto);
        return ResponseEntity.ok(sellerDto);
    }

    @PostMapping
    @Operation(summary = "Create new seller", description = "Creates a new seller and returns it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Seller created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = "{ \"timestamp\": \"2025-08-27T23:01:34\", " +
                                                    "\"message\": \"field `name` must not be blank\", " +
                                                    "\"description\": \"uri=/api/v1/seller\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }

                    )
            )
    })
    public ResponseEntity<SellerDto> create(@RequestBody @Valid SellerCreateDto sellerCreateDto) {
        log.trace("Creating new seller: sellerCreateDto={}", sellerCreateDto);
        var sellerDto = this.sellerService.create(sellerCreateDto);
        log.trace("sellerDto={}", sellerDto);
        return ResponseEntity
                .created(URI.create(API_VERSION_V1 + PATH_SELLER + "/" + sellerDto.id()))
                .body(sellerDto);
    }

    @PutMapping
    @Operation(summary = "Update seller (full)", description = "Updates all seller fields")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Seller updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = "{ \"timestamp\": \"2025-08-27T23:01:34\", " +
                                                    "\"message\": \"field `name` must not be blank\", " +
                                                    "\"description\": \"uri=/api/v1/seller\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seller not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Seller not found",
                                            value = "{ \"timestamp\": \"2025-08-27T12:34:56\", " +
                                                    "\"message\": \"There is no seller with id 42\", " +
                                                    "\"description\": \"uri=/api/v1/seller/42\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<SellerDto> put(@RequestBody @Valid SellerUpdateDto sellerUpdateDto) {
        log.trace("Updating seller: sellerUpdateDto={}", sellerUpdateDto);
        var sellerDto = this.sellerService.put(sellerUpdateDto);
        log.trace("sellerDto={}", sellerDto);
        return ResponseEntity.ok(sellerDto);
    }

    @PatchMapping
    @Operation(summary = "Update seller (partial)", description = "Updates only provided seller fields")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Seller updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = "{ \"timestamp\": \"2025-08-27T23:01:34\", " +
                                                    "\"message\": \"field `name` must not be blank\", " +
                                                    "\"description\": \"uri=/api/v1/seller\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seller not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Seller not found",
                                            value = "{ \"timestamp\": \"2025-08-27T12:34:56\", " +
                                                    "\"message\": \"There is no seller with id 42\", " +
                                                    "\"description\": \"uri=/api/v1/seller/42\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<SellerDto> patch(@RequestBody @Valid SellerPatchDto sellerPatchDto) {
        log.trace("Patching seller: sellerPatchDto={}", sellerPatchDto);
        var sellerDto = this.sellerService.patch(sellerPatchDto);
        log.trace("sellerDto={}", sellerDto);
        return ResponseEntity.ok(sellerDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seller", description = "Deletes seller by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Seller deleted successfully",
                    content = @Content(
                            schema = @Schema()
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seller not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Seller not found",
                                            value = "{ \"timestamp\": \"2025-08-27T12:34:56\", " +
                                                    "\"message\": \"There is no seller with id 42\", " +
                                                    "\"description\": \"uri=/api/v1/seller/42\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        log.trace("Deleting seller with id={}", id);
        this.sellerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
