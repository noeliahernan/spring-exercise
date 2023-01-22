package com.spring.exercise.service.impl;

import com.spring.exercise.dto.CurrencyDTO;
import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.entity.Rate;
import com.spring.exercise.exception.RateIdMismatchException;
import com.spring.exercise.exception.RateNotFoundException;
import com.spring.exercise.mapper.RateMapper;
import com.spring.exercise.repository.RateRepository;
import com.spring.exercise.service.RateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RateServiceImpl implements RateService {
    public static final String API_V1_CURRENCIES = "https://virtual/api/v1/currencies";
    public static final String CURRENCY_CODE = "currencyCode";
    private final RateRepository rateRepository;

    private final Logger log = LoggerFactory.getLogger(RateServiceImpl.class);

    @Override
    public List<RateDTO> findAllRate() {
        return rateRepository.findAll().stream().map(reg-> rateMapper.rateToRateDTO(reg)).collect(Collectors.toList());
    }

    private RestTemplate restTemplate;

    private RateMapper rateMapper;

    public RateServiceImpl(RateRepository rateRepository, RestTemplate restTemplate, RateMapper rateMapper) {
        this.rateRepository = rateRepository;
        this.restTemplate = restTemplate;
        this.rateMapper = rateMapper;
    }
    @Override
    public RateDTO findById(Long id) {
        log.info("Find rate by id:" + id);
        Rate rate = rateRepository.findById(id)
                .orElseThrow(RateNotFoundException::new);
        RateDTO rateDTO = rateMapper.rateToRateDTO(rate);
        if(rate != null && rate.getCurrencyCode()!= null){
            formatPriceAndSymbol(rateDTO);
        }
        return rateDTO;
    }

    private RateDTO formatPriceAndSymbol(RateDTO rateDTO) {
        CurrencyDTO currencyDTO = this.getDetailCurrency(rateDTO.getCurrencyCode());
        if(currencyDTO != null){
            Double priceFormatted = Double.parseDouble(String.format("%." + currencyDTO.getDecimals() + "f", rateDTO.getPrice()).replace(",","."));
            rateDTO.setPrice(priceFormatted);
            rateDTO.setSymbol(currencyDTO.getSymbol());
        }
        return rateDTO;
    }

    @Override
    public RateDTO saveRate(RateDTO rateDTO) {
        log.info("Save rate");
        return rateMapper.rateToRateDTO(rateRepository.save(rateMapper.rateDTOToRate(rateDTO)));
    }


    @Override
    public RateDTO updateRate(Long id, RateDTO rateDTO) {
        log.info("Update rate id:"+ id);
        if (rateDTO.getId().compareTo(id) != 0) {
            throw new RateIdMismatchException();
        }
        Rate rateUpdate = rateRepository.findById(id)
                .orElseThrow(RateNotFoundException::new);
        rateUpdate.setBrandId(rateDTO.getBrandId());
        rateUpdate.setPrice(rateDTO.getPrice());
        rateUpdate.setCurrencyCode(rateDTO.getCurrencyCode());
        rateUpdate.setProductId(rateDTO.getProductId());
        rateUpdate.setStartDate(rateDTO.getStartDate());
        rateUpdate.setEndDate(rateDTO.getEndDate());
        return rateMapper.rateToRateDTO(rateRepository.save(rateUpdate));
    }

    @Override
    public void deleteRate(Long id) {
        log.info("delete rate with id:"+id);
        rateRepository.deleteById(id);
    }

    @Override
    public List<RateDTO> findAllByFilter(LocalDate dateRate, Long brandId, Long productId) {
        log.info("Find active rate by date and filter brandId and productId");
        return rateRepository.findAllByFilter( dateRate,brandId, productId).stream().map(reg-> formatPriceAndSymbol(rateMapper.rateToRateDTO(reg))).collect(Collectors.toList());
    }


    private CurrencyDTO getDetailCurrency(String currencyCode) {
        log.info("Call api currency");
        ResponseEntity<CurrencyDTO> response = null;
        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_V1_CURRENCIES +"/{currencyCode}");
            response
                    = restTemplate.getForEntity(builder.buildAndExpand(Map.of(CURRENCY_CODE,currencyCode)).toUri(), CurrencyDTO.class);
        } catch (RestClientResponseException e) {
            log.error("Error calling currencies api code {} withResponseHeaders{}:{}", e.getRawStatusCode(), e.getResponseHeaders(), e);
            throw new RestClientResponseException("Error calling currencies", e.getRawStatusCode(), e.getStatusText(), e.getResponseHeaders(), e.getResponseBodyAsByteArray(), null);
        }

        return response.getBody();
    }

}
