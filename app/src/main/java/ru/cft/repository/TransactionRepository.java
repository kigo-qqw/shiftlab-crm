package ru.cft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.cft.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySellerId(Long sellerId);
}
