package com.fisa.bank.loan.presentation.controller;

import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.ApiResponseBody;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequestDTO;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponseDTO;
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
    public ApiResponse<SuccessBody<LoanProductCreateResponseDTO>> createLoanProduct(@Valid @RequestBody LoanProductCreateRequestDTO requestDTO){

        LoanProductCreateResponseDTO response = loanService.createLoanProduct(requestDTO);

        return ApiResponseGenerator.success(ResponseCode.CREATE, response);
    }

    @DeleteMapping("/products/{loanProductId}")
    public ApiResponse<SuccessBody<ResponseCode>> deleteLoanProduct(@PathVariable Long loanProductId){

        loanService.deleteLoanProduct(loanProductId);

        return ApiResponseGenerator.success(ResponseCode.DELETE);
    }


}
