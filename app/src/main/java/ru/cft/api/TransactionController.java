package ru.cft.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cft.dto.ErrorDetailsDto;
import ru.cft.dto.transaction.TransactionCreateDto;
import ru.cft.dto.transaction.TransactionDto;
import ru.cft.service.TransactionService;

import java.net.URI;
import java.util.List;

import static ru.cft.configuration.WebPath.*;

@RestController
@RequestMapping(API_VERSION_V1)
@Slf4j
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping(PATH_TRANSACTION)
    @Operation(summary = "Get all transactions", description = "Returns a list of all transactions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TransactionDto.class))
                    )
            )
    })
    public ResponseEntity<List<TransactionDto>> findAll() {
        log.trace("Fetching all sellers");
        var transactionDtos = this.transactionService.findAll();
        log.trace("transactionDtos={}", transactionDtos);
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping(PATH_TRANSACTION + "/{id}")
    @Operation(summary = "Get transaction by ID", description = "Returns transaction details for the given ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved transaction",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Transaction not found",
                                            value = "{ \"timestamp\": \"2025-08-27T12:34:56\", " +
                                                    "\"message\": \"There is no transaction with id 99\", " +
                                                    "\"description\": \"uri=/api/v1/transaction/99\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<TransactionDto> findById(@PathVariable("id") final Long id) {
        log.trace("Fetching transaction with id={}", id);
        var transactionDto = this.transactionService.findById(id);
        log.trace("transactionDto={}", transactionDto);
        return ResponseEntity.ok(transactionDto);
    }

    @PostMapping(PATH_TRANSACTION)
    @Operation(summary = "Create new transaction", description = "Creates a new transaction and returns it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDto.class)
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
                                                    "\"message\": \"field `amount` must be greater than 0\", " +
                                                    "\"description\": \"uri=/api/v1/transaction\", " +
                                                    "\"errorCode\": 400 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<TransactionDto> create(@RequestBody TransactionCreateDto dto) {
        log.trace("Creating new transaction: dto={}", dto);
        var transactionDto = this.transactionService.create(dto);
        log.trace("transactionDto={}", transactionDto);
        return ResponseEntity
                .created(URI.create(API_VERSION_V1 + PATH_TRANSACTION + "/" + transactionDto.id()))
                .body(transactionDto);
    }

    @GetMapping(PATH_SELLER + PATH_TRANSACTION + "/{id}")
    @Operation(summary = "Get transactions by seller ID", description = "Returns all transactions for the given seller ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved seller transactions",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TransactionDto.class))
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
                                                    "\"description\": \"uri=/api/v1/seller/42/transaction\", " +
                                                    "\"errorCode\": 404 }"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<List<TransactionDto>> findBySellerId(@PathVariable("id") final Long id) {
        log.trace("Fetching transactions for seller with id={}", id);
        List<TransactionDto> sellerTransactionDtos = this.transactionService.findBySellerId(id);
        log.trace("sellerTransactionDtos={}", sellerTransactionDtos);
        return ResponseEntity.ok(sellerTransactionDtos);
    }
}
