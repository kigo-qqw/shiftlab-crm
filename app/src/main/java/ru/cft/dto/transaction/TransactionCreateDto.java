package ru.cft.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.cft.enums.PaymentType;

import java.math.BigDecimal;

@Builder
public record TransactionCreateDto(
        @NotNull
        @Schema(description = "Seller identifier", example = "42")
        Long sellerId,
        @NotNull
        @DecimalMin(value = "0.0", message = "Amount must be greater than zero")
        @Schema(description = "Transaction amount", example = "199.99")
        BigDecimal amount,
        @NotNull
        @Schema(description = "Payment type", example = "CARD")
        PaymentType paymentType
) {
}
