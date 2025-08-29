package ru.cft.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.transaction.TransactionCreateDto;
import ru.cft.dto.transaction.TransactionDto;
import ru.cft.entity.Seller;
import ru.cft.entity.Transaction;
import ru.cft.enums.PaymentType;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.TransactionMapper;
import ru.cft.repository.TransactionRepository;
import ru.cft.service.SellerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private SellerService sellerService;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static final LocalDateTime FIXED_DATE =
            LocalDateTime.of(2025, 8, 28, 15, 45, 23);


    @Test
    void testThat_findAll_shouldReturnListOfTransactions() {
        var sellers = List.of(
                Seller.builder().id(1L).name("John Doe 1").contactInfo("john1@doe.com").registrationDate(FIXED_DATE).build(),
                Seller.builder().id(2L).name("John Doe 2").contactInfo("john2@doe.com").registrationDate(FIXED_DATE).build()
        );
        var transactions = List.of(
                Transaction.builder().id(1L).seller(sellers.get(0)).amount(BigDecimal.valueOf(100)).paymentType(PaymentType.CARD).transactionDate(FIXED_DATE).build(),
                Transaction.builder().id(2L).seller(sellers.get(1)).amount(BigDecimal.valueOf(200)).paymentType(PaymentType.CASH).transactionDate(FIXED_DATE).build(),
                Transaction.builder().id(3L).seller(sellers.get(1)).amount(BigDecimal.valueOf(300)).paymentType(PaymentType.TRANSFER).transactionDate(FIXED_DATE).build()
        );
        var expectedDtos = transactions.stream().map(tx ->
                TransactionDto.builder()
                        .id(tx.getId())
                        .seller(SellerDto.builder()
                                .id(tx.getSeller().getId())
                                .name(tx.getSeller().getName())
                                .contactInfo(tx.getSeller().getContactInfo())
                                .registrationDate(tx.getSeller().getRegistrationDate())
                                .build())
                        .amount(tx.getAmount())
                        .paymentType(tx.getPaymentType())
                        .transactionDate(tx.getTransactionDate())
                        .build()
        ).toList();

        when(this.transactionRepository.findAll()).thenReturn(transactions);
        when(this.transactionMapper.toDto(transactions.get(0))).thenReturn(expectedDtos.get(0));
        when(this.transactionMapper.toDto(transactions.get(1))).thenReturn(expectedDtos.get(1));
        when(this.transactionMapper.toDto(transactions.get(2))).thenReturn(expectedDtos.get(2));

        var result = this.transactionService.findAll();

        assertEquals(expectedDtos, result);
        verify(this.transactionRepository).findAll();
        verify(this.transactionMapper, times(3)).toDto(any(Transaction.class));
    }

    @Test
    void testThat_findById_shouldReturnDto_whenExists() {
        var seller = Seller.builder().id(1L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var id = 42L;
        var transaction = Transaction.builder().id(id).seller(seller).amount(BigDecimal.valueOf(100)).paymentType(PaymentType.CARD).transactionDate(FIXED_DATE).build();
        var expectedDto = TransactionDto.builder()
                .id(transaction.getId())
                .seller(SellerDto.builder()
                        .id(seller.getId())
                        .name(seller.getName())
                        .contactInfo(seller.getContactInfo())
                        .registrationDate(seller.getRegistrationDate())
                        .build())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .build();

        when(this.transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(this.transactionMapper.toDto(transaction)).thenReturn(expectedDto);

        var result = this.transactionService.findById(id);

        assertEquals(expectedDto, result);
        verify(this.transactionRepository).findById(id);
        verify(this.transactionMapper).toDto(transaction);
    }

    @Test
    void testThat_findById_shouldThrowException_whenNotExists() {
        var id = 42L;
        when(this.transactionRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.transactionService.findById(id));

        assertEquals(String.format("There is no transaction with id %d", id), exception.getMessage());
        verify(this.transactionRepository).findById(id);
        verifyNoInteractions(this.transactionMapper);
    }

    @Test
    void testThat_create_shouldSaveAndReturnDto() {
        var seller = Seller.builder().id(1L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var transactionCreateDto = TransactionCreateDto.builder().sellerId(seller.getId()).amount(BigDecimal.valueOf(150)).paymentType(PaymentType.CASH).build();
        var transaction = Transaction.builder().amount(transactionCreateDto.amount()).paymentType(transactionCreateDto.paymentType()).build();
        var saved = Transaction.builder()
                .id(42L).seller(seller).amount(transactionCreateDto.amount()).paymentType(transactionCreateDto.paymentType()).transactionDate(FIXED_DATE).build();
        var expectedDto = TransactionDto.builder()
                .id(saved.getId())
                .seller(SellerDto.builder()
                        .id(saved.getSeller().getId())
                        .name(saved.getSeller().getName())
                        .contactInfo(saved.getSeller().getContactInfo())
                        .registrationDate(saved.getSeller().getRegistrationDate())
                        .build())
                .amount(saved.getAmount()).paymentType(saved.getPaymentType())
                .transactionDate(saved.getTransactionDate())
                .build();

        when(this.transactionMapper.toEntityWithoutEnrichment(transactionCreateDto)).thenReturn(transaction);
        when(this.sellerService.findEntityById(seller.getId())).thenReturn(seller);
        when(this.transactionRepository.save(transaction)).thenReturn(saved);
        when(this.transactionMapper.toDto(saved)).thenReturn(expectedDto);

        var result = this.transactionService.create(transactionCreateDto);

        assertEquals(expectedDto, result);
        verify(this.transactionMapper).toEntityWithoutEnrichment(transactionCreateDto);
        verify(this.sellerService).findEntityById(seller.getId());
        verify(this.transactionRepository).save(transaction);
        verify(this.transactionMapper).toDto(saved);
    }

    @Test
    void testThat_create_shouldThrowException_whenSellerDoesNotExist() {
        var sellerId = 42L;
        var transactionCreateDto = TransactionCreateDto.builder()
                .sellerId(sellerId)
                .amount(BigDecimal.valueOf(150))
                .paymentType(PaymentType.CASH)
                .build();

        when(this.sellerService.findEntityById(sellerId))
                .thenThrow(new ResourceNotFoundException("There is no seller with id " + sellerId));

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.transactionService.create(transactionCreateDto));

        assertEquals("There is no seller with id 42", exception.getMessage());
        verify(this.sellerService).findEntityById(sellerId);
        verifyNoInteractions(this.transactionRepository);
        verify(this.transactionMapper).toEntityWithoutEnrichment(any());
    }

    @Test
    void testThat_findBySellerId_shouldReturnTransactions() {
        var seller = Seller.builder().id(42L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();
        var transactions = List.of(
                Transaction.builder().id(1L).seller(seller).amount(BigDecimal.valueOf(100)).paymentType(PaymentType.CARD).transactionDate(FIXED_DATE).build(),
                Transaction.builder().id(2L).seller(seller).amount(BigDecimal.valueOf(200)).paymentType(PaymentType.CASH).transactionDate(FIXED_DATE).build()
        );
        var expectedDtos = transactions.stream().map(tx ->
                TransactionDto.builder()
                        .id(tx.getId())
                        .seller(SellerDto.builder()
                                .id(tx.getSeller().getId())
                                .name(tx.getSeller().getName())
                                .contactInfo(tx.getSeller().getContactInfo())
                                .registrationDate(tx.getSeller().getRegistrationDate())
                                .build())
                        .amount(tx.getAmount())
                        .paymentType(tx.getPaymentType())
                        .transactionDate(tx.getTransactionDate())
                        .build()
        ).toList();

        when(this.transactionRepository.findAllBySellerId(seller.getId())).thenReturn(transactions);
        when(this.transactionMapper.toDto(transactions.get(0))).thenReturn(expectedDtos.get(0));
        when(this.transactionMapper.toDto(transactions.get(1))).thenReturn(expectedDtos.get(1));

        var result = transactionService.findBySellerId(seller.getId());

        assertEquals(expectedDtos, result);
        verify(this.transactionRepository).findAllBySellerId(seller.getId());
        verify(this.transactionMapper, times(2)).toDto(any(Transaction.class));
    }

    @Test
    void testThat_findBySellerId_shouldReturnEmptyList_whenSellerDoesNotExist() {
        var sellerId = 42L;

        when(sellerService.findEntityById(sellerId))
                .thenThrow(new ResourceNotFoundException("There is no seller with id " + sellerId));

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.transactionService.findBySellerId(sellerId));

        assertEquals("There is no seller with id 42", exception.getMessage());

        verify(this.sellerService).findEntityById(sellerId);
        verifyNoInteractions(this.transactionRepository, this.transactionMapper);
    }

}