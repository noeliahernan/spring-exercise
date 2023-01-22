package com.spring.exercise.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO implements Serializable {

    private static final long serialVersionUID = -3913165616387170605L;
    private char symbol;
    private String code;
    private int decimals;

}
