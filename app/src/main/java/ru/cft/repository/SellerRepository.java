package ru.cft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.cft.dto.SellerWithIncomeInternalDto;
import ru.cft.entity.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query(value = "SELECT s.*, sum_of_transaction_amount FROM transaction t " +
            "JOIN seller s ON t.seller = s.id " +
            "WHERE t.transaction_date BETWEEN :start AND :end " +
            "GROUP BY s.id " +
            "ORDER BY sum(t.amount) AS sum_of_transaction_amount DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<SellerWithIncomeInternalDto> findBestSellerByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT s.*, sum_of_transaction_amount FROM transaction t " +
            "JOIN seller s ON t.seller = s.id " +
            "WHERE t.transaction_date BETWEEN :start AND :end " +
            "GROUP BY s.id " +
            "HAVING sum(t.amount) < :threshold" +
            "ORDER BY sum(t.amount) DESC",
            nativeQuery = true)
    List<Seller> findAllSellersWithIncomeLowerThanThreshold(
            @Param("threshold") BigDecimal incomeThreshold,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );
}
