package ru.cft.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.cft.dto.ErrorDetailsDto;
import ru.cft.dto.analytics.SellerWithIncomeDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.service.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.cft.configuration.WebPath.API_VERSION_V1;
import static ru.cft.configuration.WebPath.PATH_SELLER;

@RestController
@Tag(name = "Sellers analytics")
@RequestMapping(API_VERSION_V1 + PATH_SELLER)
@Validated
@Slf4j
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/top-seller")
    @Operation(summary = "Get top seller by income", description = "Returns the seller with the highest total income for a given period")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved top seller",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SellerWithIncomeDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid date range",
                                            value = "{ \"timestamp\": \"2025-08-29T12:00:00\", " +
                                                    "\"message\": \"End date must be after start date\", " +
                                                    "\"description\": \"uri=/api/v1/seller/top-seller\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<SellerWithIncomeDto> findTopSeller(
            @Parameter(description = "Start of the period", required = true)
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(name = "start") LocalDateTime start,
            @Parameter(description = "End of the period", required = true)
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(name = "end") LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
        var sellerWithIncomeDto = this.analyticsService.findTopSellerByPeriod(start, end);
        return ResponseEntity.ok(sellerWithIncomeDto);
    }

    @GetMapping("/sellers-with-income-less-threshold")
    @Operation(summary = "Get sellers with income below threshold", description = "Returns all sellers whose total income for the given period is below the specified threshold")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved sellers",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SellerDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid threshold",
                                            value = "{ \"timestamp\": \"2025-08-29T12:00:00\", " +
                                                    "\"message\": \"Threshold must be greater than zero\", " +
                                                    "\"description\": \"uri=/api/v1/seller/sellers-with-income-less-threshold\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<List<SellerDto>> findTopSellersWithIncomeLessThanThresholdByPeriod(
            @Parameter(description = "Start of the period", required = true)
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(name = "start") LocalDateTime start,

            @Parameter(description = "End of the period", required = true)
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(name = "end") LocalDateTime end,

            @Parameter(description = "Income threshold", required = true, example = "10000")
            @NotNull
            @DecimalMin(value = "0.0", message = "Threshold must be greater than zero")
            @RequestParam(name = "threshold") BigDecimal threshold
    ) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
        var sellers = this.analyticsService.findSellersWithIncomeLessThanThresholdByPeriod(threshold, start, end);
        return ResponseEntity.ok(sellers);
    }
}
