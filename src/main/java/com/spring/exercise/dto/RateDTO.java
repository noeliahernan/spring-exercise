package com.spring.exercise.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RateDTO implements Serializable {

    private static final long serialVersionUID = -2868715158198942442L;
    @NotNull
    private Long id;
    @NotNull
    private Long brandId;
    @NotNull
    private Long productId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private double price;
    @NotNull
    @Size(max = 3)
    private String currencyCode;
    private char symbol;
}
