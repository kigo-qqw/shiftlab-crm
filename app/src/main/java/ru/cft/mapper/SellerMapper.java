package ru.cft.mapper;

import org.mapstruct.*;
import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.entity.Seller;

@Mapper(componentModel = "spring")
public interface SellerMapper {
    SellerDto toDto(Seller seller);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    Seller toEntityWithoutEnrichment(SellerCreateDto sellerCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    void updateEntity(SellerUpdateDto sellerUpdateDto, @MappingTarget Seller seller);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(SellerPatchDto sellerPatchDto, @MappingTarget Seller seller);

    Seller toEntity(ru.cft.dto.seller.SellerDto sellerDto);
}
