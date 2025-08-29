package ru.cft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cft.dto.DateRangeDto;
import ru.cft.dto.analytics.SellerWithIncomeDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.AnalyticsMapper;
import ru.cft.mapper.SellerMapper;
import ru.cft.repository.SellerRepository;
import ru.cft.service.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final SellerRepository sellerRepository;
    private final AnalyticsMapper analyticsMapper;
    private final SellerMapper sellerMapper;

    @Override
    public SellerWithIncomeDto findTopSellerByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        var seller = this.sellerRepository.findBestSellerByPeriod(startDate, endDate);
        return this.analyticsMapper.toDto(
                seller.orElseThrow(() -> {
                    var errorMessage = "There is no sellers with any transactions between " + startDate + " and " + endDate;
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                })
        );
    }

    @Override
    public List<SellerDto> findSellersWithIncomeLessThanThresholdByPeriod(BigDecimal incomeThreshold, LocalDateTime startDate, LocalDateTime endDate) {
        var sellers = this.sellerRepository.findAllSellersWithIncomeLowerThanThreshold(incomeThreshold, startDate, endDate);
        return sellers.stream().map(this.sellerMapper::toDto).toList();
    }

    @Override
    public DateRangeDto getBestPerformancePeriodForSeller(Long sellerId) {
        return null;
    }
}
