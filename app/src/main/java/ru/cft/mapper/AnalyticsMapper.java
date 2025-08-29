package ru.cft.mapper;

import org.mapstruct.Mapper;
import ru.cft.dto.SellerWithIncomeInternalDto;
import ru.cft.dto.analytics.SellerWithIncomeDto;

@Mapper(componentModel = "spring", uses = SellerMapper.class)
public interface AnalyticsMapper {
    SellerWithIncomeDto toDto(SellerWithIncomeInternalDto dto);
}
