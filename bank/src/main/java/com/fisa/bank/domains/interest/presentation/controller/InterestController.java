package com.fisa.bank.domains.interest.presentation.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisa.bank.domains.common.presentation.response.ApiResponse;
import com.fisa.bank.domains.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.domains.common.presentation.response.body.SuccessBody;
import com.fisa.bank.domains.common.presentation.response.code.ResponseCode;
import com.fisa.bank.domains.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.domains.interest.application.service.InterestService;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;

  @GetMapping("/{loanProductId}")
  public ApiResponse<SuccessBody<List<InterestRateResponse>>> findAllInterest(
      @PathVariable Long loanProductId) {

    List<InterestRateResponse> allInterestRates = interestService.findAllById(loanProductId);

    return ApiResponseGenerator.success(ResponseCode.GET, allInterestRates);
  }
}
