package ru.cft.service;

import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.entity.Seller;

import java.util.List;

public interface SellerService {
    List<SellerDto> findAll();

    SellerDto findById(Long id);

    SellerDto create(SellerCreateDto sellerCreateDto);

    void deleteById(Long id);

    SellerDto put(SellerUpdateDto sellerUpdateDto);

    SellerDto patch(SellerPatchDto sellerPatchDto);

    Seller findEntityById(Long id);
}
