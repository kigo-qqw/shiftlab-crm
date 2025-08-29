package ru.cft.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.cft.dto.SellerWithIncomeInternalDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.entity.Seller;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.AnalyticsMapper;
import ru.cft.mapper.SellerMapper;
import ru.cft.repository.SellerRepository;
import ru.cft.dto.analytics.SellerWithIncomeDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {
    @Mock
    private SellerMapper sellerMapper;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private AnalyticsMapper analyticsMapper;
    @InjectMocks
    private AnalyticsServiceImpl analyticsService;
    private static final LocalDateTime FIXED_DATE =
            LocalDateTime.of(2025, 8, 1, 15, 45, 23);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 8, 5, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2025, 8, 20, 0, 0, 0);


    @Test
    void testThat_findTopSellerByPeriod_shouldReturnDto_whenSellerExists() {
        var seller = Seller.builder()
                .id(1L)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .registrationDate(FIXED_DATE)
                .build();
        var sellerWithIncomeInternalDto = SellerWithIncomeInternalDto.builder().seller(seller).sumOfTransactionAmount(BigDecimal.valueOf(1000)).build();
        var expectedDto = SellerWithIncomeDto.builder()
                .seller(SellerDto.builder()
                        .id(seller.getId())
                        .name(seller.getName())
                        .contactInfo(seller.getContactInfo())
                        .registrationDate(seller.getRegistrationDate())
                        .build())
                .sumOfTransactionAmount(sellerWithIncomeInternalDto.sumOfTransactionAmount())
                .build();

        when(this.sellerRepository.findBestSellerByPeriod(START_DATE, END_DATE)).thenReturn(Optional.of(sellerWithIncomeInternalDto));
        when(this.analyticsMapper.toDto(sellerWithIncomeInternalDto)).thenReturn(expectedDto);

        var result = this.analyticsService.findTopSellerByPeriod(START_DATE, END_DATE);

        assertEquals(expectedDto, result);
        verify(this.sellerRepository).findBestSellerByPeriod(START_DATE, END_DATE);
        verify(this.analyticsMapper).toDto(sellerWithIncomeInternalDto);
    }

    @Test
    void testThat_findTopSellerByPeriod_shouldThrowException_whenNoSellerExists() {
        when(this.sellerRepository.findBestSellerByPeriod(START_DATE, END_DATE)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.analyticsService.findTopSellerByPeriod(START_DATE, END_DATE));

        assertEquals("There is no sellers with any transactions between " + START_DATE + " and " + END_DATE,
                exception.getMessage());
        verify(this.sellerRepository).findBestSellerByPeriod(START_DATE, END_DATE);
        verifyNoInteractions(this.analyticsMapper);
    }

    @Test
    void testThat_findSellersWithIncomeLessThanThresholdByPeriod_shouldReturnListOfDtos() {
        var incomeThreshold = BigDecimal.valueOf(500);
        var sellers = List.of(
                Seller.builder().id(1L).name("John Doe 1").contactInfo("john@doe1.com").build(),
                Seller.builder().id(2L).name("John Doe 2").contactInfo("john@doe2.com").build()
        );
        var expectedDtos = sellers.stream().map(s ->
                SellerDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .contactInfo(s.getContactInfo())
                        .build()
        ).toList();

        when(this.sellerRepository.findAllSellersWithIncomeLowerThanThreshold(incomeThreshold, START_DATE, END_DATE))
                .thenReturn(sellers);
        when(this.sellerMapper.toDto(sellers.get(0))).thenReturn(expectedDtos.get(0));
        when(this.sellerMapper.toDto(sellers.get(1))).thenReturn(expectedDtos.get(1));

        var result = this.analyticsService.findSellersWithIncomeLessThanThresholdByPeriod(incomeThreshold, START_DATE, END_DATE);

        assertEquals(expectedDtos, result);
        verify(this.sellerRepository).findAllSellersWithIncomeLowerThanThreshold(incomeThreshold, START_DATE, END_DATE);
        verify(this.sellerMapper, times(2)).toDto(any(Seller.class));
    }

    @Test
    void testThat_findSellersWithIncomeLessThanThresholdByPeriod_shouldReturnEmptyList_whenNoSellers() {
        var incomeThreshold = BigDecimal.valueOf(500);

        when(this.sellerRepository.findAllSellersWithIncomeLowerThanThreshold(incomeThreshold, START_DATE, END_DATE))
                .thenReturn(List.of());

        var result = this.analyticsService.findSellersWithIncomeLessThanThresholdByPeriod(incomeThreshold, START_DATE, END_DATE);

        assertTrue(result.isEmpty());
        verify(this.sellerRepository).findAllSellersWithIncomeLowerThanThreshold(incomeThreshold, START_DATE, END_DATE);
        verifyNoInteractions(this.sellerMapper);
    }
}
