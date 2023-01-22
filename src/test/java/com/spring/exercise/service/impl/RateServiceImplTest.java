package com.spring.exercise.service.impl;

import com.spring.exercise.dto.CurrencyDTO;
import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.entity.Rate;
import com.spring.exercise.mapper.RateMapper;
import com.spring.exercise.repository.RateRepository;
import com.spring.exercise.service.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateServiceImplTest {
    private static final Long DEFAULT_ID = 1L;
    private static final Long UPDATED_ID = 2L;

    private static final String DEFAULT_STRING = "AAAAAAAAAA";
    private static final String UPDATED_STRING = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "EUR";
    private static final String UPDATED_CURRENCY = "GBR";

    private static final LocalDate DEFAULT_DATE = LocalDate.now();
    private static final LocalDate UPDATE_DATE = LocalDate.now();

    private static final double DEFAULT_PRICE = 2.3;
    private static final double UPDATE_PRICE = 4.2;


    RateRepository rateRepository;
    RateService rateService;

    RateMapper rateMapper;

    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        rateRepository = mock(RateRepository.class);
        rateMapper = mock(RateMapper.class);
        restTemplate = mock(RestTemplate.class);
        rateService = new RateServiceImpl(rateRepository, restTemplate, rateMapper);
    }

    public static RateDTO createDTO() {
        RateDTO rateDTO = RateDTO.builder().id(DEFAULT_ID).brandId(DEFAULT_ID)
                .productId(DEFAULT_ID)
                .price(DEFAULT_PRICE)
                .currencyCode(DEFAULT_CURRENCY)
                .startDate(DEFAULT_DATE)
                .endDate(DEFAULT_DATE).build();
        return rateDTO;
    }

    public static RateDTO createUpdateDTO() {
        RateDTO rateDTO = RateDTO.builder().brandId(UPDATED_ID)
                .productId(UPDATED_ID)
                .price(UPDATE_PRICE)
                .currencyCode(UPDATED_CURRENCY)
                .startDate(UPDATE_DATE)
                .endDate(UPDATE_DATE).build();
        return rateDTO;
    }

    public static Rate createEntity() {
        Rate rate = new Rate();
        rate.setId(DEFAULT_ID);
        rate.setBrandId(DEFAULT_ID);
        rate.setProductId(DEFAULT_ID);
        rate.setPrice(DEFAULT_PRICE);
        rate.setCurrencyCode(DEFAULT_CURRENCY);
        rate.setStartDate(DEFAULT_DATE);
        rate.setEndDate(DEFAULT_DATE);
        return rate;
    }


    @Test
    void findAllByFilter() {
        Rate rate = createEntity();
        RateDTO rateDTO = createDTO();

        when(rateMapper.rateToRateDTO(any())).thenReturn(rateDTO);
        when(rateMapper.rateDTOToRate(any())).thenReturn(rate);
        when(restTemplate.getForEntity(any(),any())).thenReturn(new ResponseEntity<>(CurrencyDTO.builder().symbol('€').code("EUR").decimals(2).build(), HttpStatus.OK));

        List<Rate> rateList = new ArrayList<>();
        rateList.add(rate);
        when(rateRepository.findAllByFilter(any(), any(), any())).thenReturn(rateList);
        List<RateDTO> result = rateService.findAllByFilter(null,null,null);
        assertEquals(result.get(0).getBrandId(), rateDTO.getBrandId());

    }

    @Test
    void findById() {
        Rate rate = createEntity();
        RateDTO rateDTO = createDTO();
        when(rateMapper.rateToRateDTO(any())).thenReturn(rateDTO);
        when(restTemplate.getForEntity(any(),any())).thenReturn(new ResponseEntity<>(CurrencyDTO.builder().symbol('€').code("EUR").decimals(2).build(), HttpStatus.OK));
        when(rateRepository.findById(any())).thenReturn(Optional.of(rate));
        RateDTO result = rateService.findById(DEFAULT_ID);
        assertEquals(result.getId(), DEFAULT_ID);

    }

    @Test
    void saveRate() {
        Rate rate = createEntity();
        RateDTO rateDTO = createDTO();
        when(rateMapper.rateToRateDTO(any())).thenReturn(rateDTO);
        when(rateMapper.rateDTOToRate(any())).thenReturn(rate);
        when(rateRepository.save(any())).thenReturn(rate);
        RateDTO result = rateService.saveRate(rateDTO);
        assertEquals(result.getId(), DEFAULT_ID);
    }

    @Test
    void updateRate() {
        Rate rate = createEntity();
        RateDTO rateDTO = createDTO();
        when(rateMapper.rateToRateDTO(any())).thenReturn(rateDTO);
        when(rateMapper.rateDTOToRate(any())).thenReturn(rate);
        when(rateRepository.findById(any())).thenReturn(Optional.of(rate));
        when(rateRepository.save(any())).thenReturn(rate);
        RateDTO result = rateService.updateRate(DEFAULT_ID,rateDTO);
        assertEquals(result.getId(), DEFAULT_ID);
    }


}