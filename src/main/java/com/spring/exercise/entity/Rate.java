package com.spring.exercise.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "T_RATES")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column
    private Long brandId;
    @Column
    private Long productId;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    private double price;
    @Column
    private String currencyCode;
}
