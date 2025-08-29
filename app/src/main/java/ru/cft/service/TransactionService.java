package ru.cft.service;

import ru.cft.dto.transaction.TransactionCreateDto;
import ru.cft.dto.transaction.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> findAll();

    TransactionDto findById(Long id);

    TransactionDto create(TransactionCreateDto transactionCreateDto);

    List<TransactionDto> findBySellerId(Long sellerId);
}
