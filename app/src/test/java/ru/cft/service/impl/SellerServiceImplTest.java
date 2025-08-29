package ru.cft.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.entity.Seller;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.SellerMapper;
import ru.cft.repository.SellerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private SellerMapper sellerMapper;
    @InjectMocks
    private SellerServiceImpl sellerService;
    private static final LocalDateTime FIXED_DATE =
            LocalDateTime.of(2025, 8, 28, 15, 45, 23);

    @Test
    void testThat_findAll_shouldReturnListOfSellers() {
        var sellers = List.of(
                Seller.builder().id(1L).name("John Doe 1").contactInfo("john1@doe.com").registrationDate(FIXED_DATE).build(),
                Seller.builder().id(2L).name("John Doe 2").contactInfo("john2@doe.com").registrationDate(FIXED_DATE).build()
        );
        var expectedDtos = sellers.stream().map(seller ->
                SellerDto.builder()
                        .id(seller.getId())
                        .name(seller.getName())
                        .contactInfo(seller.getContactInfo())
                        .registrationDate(seller.getRegistrationDate())
                        .build()
        ).toList();

        when(this.sellerRepository.findAll()).thenReturn(sellers);
        when(this.sellerMapper.toDto(sellers.get(0))).thenReturn(expectedDtos.get(0));
        when(this.sellerMapper.toDto(sellers.get(1))).thenReturn(expectedDtos.get(1));

        var result = this.sellerService.findAll();

        assertEquals(expectedDtos, result);
        verify(this.sellerRepository, times(1)).findAll();
        verify(this.sellerMapper, times(2)).toDto(any(Seller.class));
    }

    @Test
    void testThat_findById_shouldReturnDto_whenExists() {
        var id = 42L;
        var seller = Seller.builder().id(id).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var expectedDto = SellerDto.builder()
                .id(seller.getId())
                .name(seller.getName())
                .contactInfo(seller.getContactInfo())
                .registrationDate(seller.getRegistrationDate())
                .build();

        when(this.sellerRepository.findById(id)).thenReturn(Optional.of(seller));
        when(this.sellerMapper.toDto(seller)).thenReturn(expectedDto);

        var result = this.sellerService.findById(id);

        assertEquals(expectedDto, result);
        verify(sellerRepository, times(1)).findById(id);
        verify(sellerMapper, times(1)).toDto(seller);
    }

    @Test
    void testThat_findById_shouldThrowException_whenNotExists() {
        var id = 42L;
        when(this.sellerRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.sellerService.findById(id));

        assertEquals(String.format("There is no seller with id %d", id), exception.getMessage());
        verify(this.sellerRepository, times(1)).findById(id);
        verifyNoInteractions(this.sellerMapper);
    }

    @Test
    void testThat_create_shouldSaveAndReturnDto() {
        var sellerCreateDto = SellerCreateDto.builder().name("John Doe").contactInfo("john@doe.com").build();
        var seller = Seller.builder().name(sellerCreateDto.name()).contactInfo(sellerCreateDto.contactInfo()).build();
        var saved = Seller.builder().id(42L).name(sellerCreateDto.name()).contactInfo(sellerCreateDto.contactInfo()).registrationDate(FIXED_DATE).build();
        var expectedDto = SellerDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .contactInfo(saved.getContactInfo())
                .registrationDate(saved.getRegistrationDate())
                .build();

        when(this.sellerMapper.toEntityWithoutEnrichment(sellerCreateDto)).thenReturn(seller);
        when(this.sellerRepository.save(seller)).thenReturn(saved);
        when(this.sellerMapper.toDto(saved)).thenReturn(expectedDto);

        var result = this.sellerService.create(sellerCreateDto);

        assertEquals(expectedDto, result);
        verify(this.sellerMapper).toEntityWithoutEnrichment(sellerCreateDto);
        verify(this.sellerRepository).save(seller);
        verify(this.sellerMapper).toDto(saved);
    }

    @Test
    void testThat_deleteById_shouldDelete_whenExists() {
        var id = 42L;
        var seller = Seller.builder().id(id).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();

        when(this.sellerRepository.findById(id)).thenReturn(Optional.of(seller));

        this.sellerService.deleteById(id);

        verify(this.sellerRepository).deleteById(id);
    }

    @Test
    void testThat_deleteById_shouldThrowException_whenNotExists() {
        var id = 42L;
        when(this.sellerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> this.sellerService.deleteById(id));

        verify(this.sellerRepository, never()).deleteById(any());
    }

    @Test
    void testThat_put_shouldUpdateAndReturnDto_whenExists() {
        var seller = Seller.builder().id(42L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var sellerUpdateDto = SellerUpdateDto.builder().id(seller.getId()).name("Updated Doe").contactInfo("updated@doe.com").build();
        var updatedSeller = Seller.builder()
                .id(seller.getId())
                .name(sellerUpdateDto.name())
                .contactInfo(sellerUpdateDto.contactInfo())
                .registrationDate(seller.getRegistrationDate())
                .build();
        var expectedDto = SellerDto.builder()
                .id(updatedSeller.getId())
                .name(updatedSeller.getName())
                .contactInfo(updatedSeller.getContactInfo())
                .registrationDate(updatedSeller.getRegistrationDate())
                .build();

        when(this.sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        doAnswer(invocation -> {
            seller.setName(sellerUpdateDto.name());
            seller.setContactInfo(sellerUpdateDto.contactInfo());
            return null;
        }).when(this.sellerMapper).updateEntity(sellerUpdateDto, seller);
        when(this.sellerRepository.save(seller)).thenReturn(updatedSeller);
        when(this.sellerMapper.toDto(updatedSeller)).thenReturn(expectedDto);

        var result = this.sellerService.put(sellerUpdateDto);

        assertEquals(expectedDto, result);
        verify(this.sellerMapper).updateEntity(sellerUpdateDto, seller);
        verify(this.sellerRepository).save(seller);
    }

    @Test
    void testThat_put_shouldThrowException_whenSellerDoesNotExist() {
        var id = 42L;
        var sellerUpdateDto = SellerUpdateDto.builder().id(id).name("Updated Doe").contactInfo("updated@doe.com").build();

        when(this.sellerRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.sellerService.put(sellerUpdateDto));

        assertEquals(String.format("There is no seller with id %d", id), exception.getMessage());
        verify(this.sellerRepository).findById(id);
        verify(this.sellerRepository, never()).save(any());
        verifyNoInteractions(this.sellerMapper);
    }

    @Test
    void testThat_patch_shouldPatchAndReturnDto_whenExists() {
        var seller = Seller.builder().id(42L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var sellerPatchDto = SellerPatchDto.builder().id(seller.getId()).name("Updated Doe").build();
        var patchedSeller = Seller.builder()
                .id(seller.getId())
                .name(sellerPatchDto.name())
                .contactInfo(seller.getContactInfo())
                .registrationDate(seller.getRegistrationDate())
                .build();
        var expectedDto = SellerDto.builder()
                .id(patchedSeller.getId())
                .name(patchedSeller.getName())
                .contactInfo(patchedSeller.getContactInfo())
                .registrationDate(patchedSeller.getRegistrationDate())
                .build();

        when(this.sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        doAnswer(invocation -> {
            seller.setName(sellerPatchDto.name());
            return null;
        }).when(this.sellerMapper).patchEntity(sellerPatchDto, seller);
        when(this.sellerRepository.save(seller)).thenReturn(patchedSeller);
        when(this.sellerMapper.toDto(patchedSeller)).thenReturn(expectedDto);

        var result = this.sellerService.patch(sellerPatchDto);

        assertEquals(expectedDto, result);
        verify(this.sellerMapper).patchEntity(sellerPatchDto, seller);
        verify(this.sellerRepository).save(seller);
    }

    @Test
    void testThat_patch_shouldThrowException_whenSellerDoesNotExist() {
        var id = 42L;
        var sellerPatchDto = SellerPatchDto.builder().id(id).name("Updated Doe").build();

        when(this.sellerRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.sellerService.patch(sellerPatchDto));

        assertEquals(String.format("There is no seller with id %d", id), exception.getMessage());
        verify(this.sellerRepository).findById(id);
        verify(this.sellerRepository, never()).save(any());
        verifyNoInteractions(this.sellerMapper);
    }

    @Test
    void testThat_findEntityById_shouldReturnEntity_whenExists() {
        var id = 42L;
        var seller = Seller.builder().id(id).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();

        when(this.sellerRepository.findById(id)).thenReturn(Optional.of(seller));

        var result = this.sellerService.findEntityById(id);

        assertEquals(seller, result);
        verify(this.sellerRepository).findById(id);
    }

    @Test
    void testThat_findEntityById_shouldThrowException_whenNotExists() {
        var id = 42L;
        when(this.sellerRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.sellerService.findEntityById(id));

        assertEquals(String.format("There is no seller with id %d", id), exception.getMessage());
        verify(this.sellerRepository).findById(id);
    }
}
