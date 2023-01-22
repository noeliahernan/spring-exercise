package com.spring.exercise.mapper;

import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.entity.Rate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RateMapper {

    RateDTO rateToRateDTO(Rate rate);

    Rate rateDTOToRate(RateDTO rateDTO);
}
