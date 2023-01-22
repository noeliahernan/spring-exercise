package com.spring.exercise.repository;

import com.spring.exercise.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    @Query(value = "SELECT rate From Rate rate WHERE :dateRate BETWEEN rate.startDate AND rate.endDate OR coalesce(:dateRate, null) is null " +
            "AND (:productId is null OR rate.productId =:productId) " +
            "AND (:brandId is null OR rate.brandId = :brandId)")
    List<Rate> findAllByFilter(@Param("dateRate") LocalDate dateRate, @Param("brandId") Long brandId, @Param("productId") Long productId);
}
