package ru.cft.service;

import ru.cft.dto.DateRangeDto;
import ru.cft.dto.analytics.SellerWithIncomeDto;
import ru.cft.dto.seller.SellerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsService {
    SellerWithIncomeDto findTopSellerByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<SellerDto> findSellersWithIncomeLessThanThresholdByPeriod(BigDecimal incomeThreshold, LocalDateTime startDate, LocalDateTime endDate);

    DateRangeDto getBestPerformancePeriodForSeller(Long sellerId);
}
