package com.fisa.bank.loan.presentation.controller;

import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.application.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ApiResponse<SuccessBody<LoanProductCreateResponse>> createLoanProduct(@Valid @RequestBody LoanProductCreateRequest requestDTO){

        LoanProductCreateResponse response = loanService.createLoanProduct(requestDTO);

        return ApiResponseGenerator.success(ResponseCode.CREATE, response);
    }

}
