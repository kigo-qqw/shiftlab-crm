package ru.cft.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.cft.dto.transaction.TransactionDto;
import ru.cft.dto.transaction.TransactionCreateDto;
import ru.cft.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    Transaction toEntityWithoutEnrichment(TransactionCreateDto transactionCreateDto);

    Transaction toEntity(TransactionDto transactionDto);
}
