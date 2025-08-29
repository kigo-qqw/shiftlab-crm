package ru.cft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.entity.Seller;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.SellerMapper;
import ru.cft.repository.SellerRepository;
import ru.cft.service.SellerService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Override
    public List<SellerDto> findAll() {
        var sellers = this.sellerRepository.findAll();
        log.debug("sellers={}", sellers);
        return sellers.stream().map(this.sellerMapper::toDto).toList();
    }

    @Override
    public SellerDto findById(Long id) {
        var seller = this.findEntityById(id);
        log.debug("seller={}", seller);
        return this.sellerMapper.toDto(seller);
    }

    @Override
    public SellerDto create(SellerCreateDto sellerCreateDto) {
        var seller = this.sellerMapper.toEntityWithoutEnrichment(sellerCreateDto);
        seller.setRegistrationDate(LocalDateTime.now());
        log.debug("enriched seller={}", seller);
        var createdSeller = this.sellerRepository.save(seller);
        log.debug("createdSeller={}", createdSeller);
        return this.sellerMapper.toDto(createdSeller);
    }

    @Override
    public void deleteById(Long id) {
        var seller = this.findEntityById(id);
        log.debug("seller={}", seller);
        this.sellerRepository.deleteById(seller.getId());
    }

    @Override
    public SellerDto put(SellerUpdateDto sellerUpdateDto) {
        var seller = this.findEntityById(sellerUpdateDto.id());
        log.debug("seller={}", seller);
        this.sellerMapper.updateEntity(sellerUpdateDto, seller);
        var updatedSeller = this.sellerRepository.save(seller);
        log.debug("updatedSeller={}", updatedSeller);
        return this.sellerMapper.toDto(updatedSeller);
    }

    @Override
    public SellerDto patch(SellerPatchDto sellerPatchDto) {
        var seller = this.findEntityById(sellerPatchDto.id());
        log.debug("seller={}", seller);
        this.sellerMapper.patchEntity(sellerPatchDto, seller);
        var patchedSeller = this.sellerRepository.save(seller);
        log.debug("patchedSeller={}", patchedSeller);
        return this.sellerMapper.toDto(patchedSeller);
    }

    @Override
    public Seller findEntityById(Long id) {
        return this.sellerRepository.findById(id).orElseThrow(() -> {
            var errorMessage = String.format("There is no seller with id %d", id);
            log.error(errorMessage);
            return new ResourceNotFoundException(errorMessage);
        });
    }
}
