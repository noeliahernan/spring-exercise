package com.spring.exercise.controller;

import com.spring.exercise.dto.RateDTO;
import com.spring.exercise.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rate")
public class RateController {
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    /**
     * Find by Id
     *
     * @param id
     * @return
     */
    @Operation(summary = "Search a rate by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Rate not found",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public RateDTO findRateById(@PathVariable("id") Long id) {
        return rateService.findById(id);
    }


    /**
     * Search all rate by filter
     *
     * @param dateRate
     * @param productId
     * @param brandId
     * @return
     */
    @Operation(summary = "Search all rate by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Rate not found",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server",
                    content = @Content)
    })
    @GetMapping
    public List<RateDTO> findAllByFilter(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateRate,
                                            @RequestParam(required = false) final Long productId,
                                            @RequestParam(required = false) final Long brandId) {
        return rateService.findAllByFilter(dateRate, brandId, productId);
    }

    /**
     * Save rate
     *
     * @param rateDTO
     * @return
     */
    @Operation(summary = "Create rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Rate created",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Error intern",
                    content = @Content),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RateDTO saveRate(@RequestBody @Valid RateDTO rateDTO) {
        return rateService.saveRate(rateDTO);
    }

    /**
     * update rate
     *
     * @param id
     * @param rateDTO
     * @return
     */
    @Operation(summary = "Update rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Rate updated",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Error intern",
                    content = @Content),
    })
    @PutMapping("/{id}")
    @ResponseBody
    public RateDTO updateRate(@PathVariable Long id, @RequestBody @Valid RateDTO rateDTO) {
        return rateService.updateRate(id, rateDTO);
    }


    /**
     * Delete rate
     *
     * @param id
     */
    @Operation(summary = "Update rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "No content",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Not found rate",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRate(@PathVariable("id") Long id) {
        rateService.deleteRate(id);
    }

}