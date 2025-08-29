package ru.cft.dto;

import lombok.Builder;
import ru.cft.entity.Seller;

import java.math.BigDecimal;

@Builder
public record SellerWithIncomeInternalDto(
        Seller seller,
        BigDecimal sumOfTransactionAmount
) {
}
