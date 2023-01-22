package com.spring.exercise.service;

import com.spring.exercise.dto.RateDTO;

import java.time.LocalDate;
import java.util.List;

public interface RateService {
    /**
     * find all
     * @return
     */
    List<RateDTO> findAllRate();

    /**
     * find by id
     * @param id
     * @return
     */
    RateDTO findById(Long id);

    /**
     * save rate
     * @param rateDTO
     * @return
     */
    RateDTO saveRate(RateDTO rateDTO);

    /**
     * updateRate
     * @param rateDTO
     * @return
     */
    RateDTO updateRate(Long id, RateDTO rateDTO);

    /**
     * deleteRate
     * @param id
     */
    void deleteRate(Long id);

    List<RateDTO> findAllByFilter(LocalDate dateRate, Long brandId, Long productId);
}
