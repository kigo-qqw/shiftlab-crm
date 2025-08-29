package ru.cft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.cft.dto.transaction.TransactionCreateDto;
import ru.cft.dto.transaction.TransactionDto;
import ru.cft.entity.Transaction;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.mapper.TransactionMapper;
import ru.cft.repository.TransactionRepository;
import ru.cft.service.SellerService;
import ru.cft.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final SellerService sellerService;

    @Override
    public List<TransactionDto> findAll() {
        var transactions = this.transactionRepository.findAll();
        return transactions.stream().map(this.transactionMapper::toDto).toList();
    }

    @Override
    public TransactionDto findById(Long id) {
        var transaction = this.transactionRepository.findById(id).orElseThrow(() -> {
            var errorMessage = String.format("There is no transaction with id %d", id);
            log.error(errorMessage);
            return new ResourceNotFoundException(errorMessage);
        });
        return this.transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto create(TransactionCreateDto transactionCreateDto) {
        var transaction = this.transactionMapper.toEntityWithoutEnrichment(transactionCreateDto);
        var seller = this.sellerService.findEntityById(transactionCreateDto.sellerId());

        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSeller(seller);

        var createdTransaction = this.transactionRepository.save(transaction);
        return this.transactionMapper.toDto(createdTransaction);
    }

    @Override
    public List<TransactionDto> findBySellerId(Long sellerId) {
        this.sellerService.findEntityById(sellerId);
        List<Transaction> transactions = this.transactionRepository.findAllBySellerId(sellerId);
        return transactions.stream().map(this.transactionMapper::toDto).toList();
    }
}
