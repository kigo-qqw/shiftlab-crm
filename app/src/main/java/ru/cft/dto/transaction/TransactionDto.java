package ru.cft.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.cft.dto.seller.SellerDto;
import ru.cft.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionDto(
        @NotNull
        @Schema(description = "Transaction identifier", example = "42")
        Long id,
        @Valid
        @Schema(description = "Seller details associated with the transaction")
        SellerDto seller,
        @NotNull
        @DecimalMin(value = "0.0", message = "Amount must be greater than zero")
        @Schema(description = "Transaction amount", example = "199.99")
        BigDecimal amount,
        @NotNull
        @Schema(description = "Payment type", example = "CARD")
        PaymentType paymentType,
        @NotNull
        @Schema(description = "Transaction date (UTC)", example = "2025-08-27T12:34:56")
        LocalDateTime transactionDate
) {
}
