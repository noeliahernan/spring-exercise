package com.spring.exercise.controller;

import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.service.RateService;
import org.h2.command.dml.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RateController.class)
public class RateControllerTest {
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

    private static final String ENTITY_API_URL_ID = "/rate/{id}";
    private static final String ENTITY_API_URL = "/rate";

    @Autowired
    MockMvc mockMvc;
    @MockBean
    RateService rateService;

    @BeforeEach
    void setUp() {
    }

    public static RateDTO createDTO() {
        RateDTO rateDTO = RateDTO.builder().brandId(DEFAULT_ID)
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

    @Test
    void findRateByIdTest() throws Exception {
        RateDTO rateDTO = RateDTO.builder().id(DEFAULT_ID).currencyCode(DEFAULT_CURRENCY).build();
        when(rateService.findById(DEFAULT_ID)).thenReturn(rateDTO);
        mockMvc.perform(MockMvcRequestBuilders.get(ENTITY_API_URL_ID, DEFAULT_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.longValue()))
                .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY));
        verify(rateService).findById(1L);
    }

    @Test
    void saveRateTest() throws Exception {
        RateDTO rateDTO = createDTO();
        when(rateService.saveRate(any())).then(inv -> {
            RateDTO r = inv.getArgument(0);
            r.setId(DEFAULT_ID);
            return r;
        });
        mockMvc.perform(MockMvcRequestBuilders.post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.brandId").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.productId").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
                .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY))
                .andExpect(jsonPath("$.startDate").value(DEFAULT_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(DEFAULT_DATE.toString()));
    }

    @Test
    void updateRateTest() throws Exception {
        RateDTO updateRateDTO = createUpdateDTO();
        when(rateService.updateRate(any(), any())).then((inv -> {
            RateDTO r = inv.getArgument(1);
            r.setId(UPDATED_ID);
            return r;
        }));
        mockMvc.perform(MockMvcRequestBuilders.put(ENTITY_API_URL_ID, UPDATED_ID).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(updateRateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(UPDATED_ID.intValue()))
                .andExpect(jsonPath("$.brandId").value(UPDATED_ID.intValue()))
                .andExpect(jsonPath("$.productId").value(UPDATED_ID.intValue()))
                .andExpect(jsonPath("$.price").value(UPDATE_PRICE))
                .andExpect(jsonPath("$.currencyCode").value(UPDATED_CURRENCY))
                .andExpect(jsonPath("$.startDate").value(UPDATE_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(UPDATE_DATE.toString()));

    }

    @Test
    void findAllByFilterTest() throws Exception {
        RateDTO rateDTO = createDTO();
        List<RateDTO> rateDTOList = new ArrayList<>();
        rateDTOList.add(rateDTO);
        when(rateService.findAllByFilter(any(), any(), any())).thenReturn(rateDTOList);
        mockMvc.perform(MockMvcRequestBuilders.get(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    void deleteByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ENTITY_API_URL_ID, DEFAULT_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}