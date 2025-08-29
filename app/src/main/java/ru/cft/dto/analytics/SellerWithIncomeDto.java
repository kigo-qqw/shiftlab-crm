package ru.cft.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.cft.dto.seller.SellerDto;

import java.math.BigDecimal;

@Builder
public record SellerWithIncomeDto(
        @Schema(description = "Seller details")
        SellerDto seller,
        @Schema(description = "Total income from all transactions", example = "15342.75")
        BigDecimal sumOfTransactionAmount
) {
}
