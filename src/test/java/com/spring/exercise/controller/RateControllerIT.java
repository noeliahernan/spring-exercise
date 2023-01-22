package com.spring.exercise.controller;

import com.spring.exercise.IntegrationTest;
import com.spring.exercise.dto.CurrencyDTO;
import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.entity.Rate;
import com.spring.exercise.mapper.RateMapper;
import com.spring.exercise.repository.RateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityManager;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RateControllerIT {

    public static final char € = '€';
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
    private static final String ENTITY_API_URL = "/rate";
    private static final String ENTITY_API_URL_ID = "/rate/{id}";

    public static final String API_V1_CURRENCIES = "https://virtual/api/v1/currencies";
    public static final String EUR = "EUR";

    private RateDTO rateDTO;

    private MockRestServiceServer mockServer;
    @Autowired
    private EntityManager em;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private MockMvc restRateMockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RateMapper rateMapper;

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

    @BeforeEach
    public void initTest() {
        rateDTO = createDTO();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    @Transactional
    void createRate() throws Exception {
        int databaseSizeBeforeCreate = rateRepository.findAll().size();
        // Create the Rate
        restRateMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rateDTO))
                )
                .andExpect(status().isCreated());

        // Validate the Rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeCreate + 1);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getBrandId()).isEqualTo(DEFAULT_ID.intValue());
    }

    @Test
    @Transactional
    void getAllRateByWithoutFilter() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get all the personList
        restRateMockMvc
                .perform(get(ENTITY_API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rateDTO.getId().intValue())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getAllRateByFilterDate() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get all the personList
        restRateMockMvc
                .perform(get(ENTITY_API_URL + "?dateRate=" + rateDTO.getStartDate().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rateDTO.getId().intValue())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem("€")))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
        mockServer.verify();
    }

    @Test
    @Transactional
    void getAllRateByFilterBrandId() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get all the personList
        restRateMockMvc
                .perform(get(ENTITY_API_URL + "?brandId=" + 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rateDTO.getId().intValue())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem("€")))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getAllRateByFilterProductId() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get all the personList
        restRateMockMvc
                .perform(get(ENTITY_API_URL + "?productId=" + 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rateDTO.getId().intValue())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem("€")))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getAllRateByFilterRateDate() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get all the personList
        restRateMockMvc
                .perform(get(ENTITY_API_URL + "?rateDate=" + LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rateDTO.getId().intValue())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].brandId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem("€")))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_DATE.toString())));
    }


    @Test
    @Transactional
    void getRateById() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));
        mockApiCurrency();
        // Get the rate
        restRateMockMvc.perform(get(ENTITY_API_URL_ID, rateDTO.getId())).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(rateDTO.getId().intValue())).andExpect(jsonPath("$.brandId").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.productId").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
                .andExpect(jsonPath("$.currencyCode").value((DEFAULT_CURRENCY)))
                .andExpect(jsonPath("$.startDate").value(DEFAULT_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(DEFAULT_DATE.toString()))
                .andExpect(jsonPath("$.symbol").value("€"));
        mockServer.verify();
    }

    @Test
    @Transactional
    void getNonExistingRate() throws Exception {
        // Get the rate
        restRateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    void putNewRate() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));

        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the Rate
        RateDTO updateRateDTO = rateMapper.rateToRateDTO(rateRepository.findById(rateDTO.getId()).get());
        updateRateDTO.setBrandId(UPDATED_ID);
        restRateMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updateRateDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updateRateDTO))
                )
                .andExpect(status().isOk());

        // Validate the rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rateList.get(rateList.size() - 1);
        assertThat(testRate.getBrandId()).isEqualTo(UPDATED_ID);
    }

    @Test
    @Transactional
    void putNonExistingRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rateDTO.setId(123L);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRateMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, rateDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(rateDTO))
                )
                .andExpect(status().isNotFound());

        // Validate the PoliticalGroup in the database
        List<Rate> politicalGroupList = rateRepository.findAll();
        assertThat(politicalGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRate() throws Exception {
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();
        rateDTO.setId(12345L);

        // If url ID doesn't match entity ID, it will throw
        restRateMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(rateDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the rate in the database
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRate() throws Exception {
        // Initialize the database
        rateDTO = rateMapper.rateToRateDTO(rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO)));

        int databaseSizeBeforeDelete = rateRepository.findAll().size();

        // Delete the Rate
        restRateMockMvc
                .perform(delete(ENTITY_API_URL_ID, rateDTO.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rate> rateList = rateRepository.findAll();
        assertThat(rateList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteRateException() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rateMapper.rateDTOToRate(rateDTO));
        // Delete the rate
        restRateMockMvc
                .perform(delete(ENTITY_API_URL_ID, "1234L").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void mockApiCurrency() throws Exception {
        CurrencyDTO currencyDTO = CurrencyDTO.builder().code(EUR)
                .symbol(€).decimals(2).build();
        mockServer.expect(requestTo(new URI(API_V1_CURRENCIES + "/EUR")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(currencyDTO))
                );
    }

    @AfterEach
    void tearDown() {
        mockServer.reset();
    }


}