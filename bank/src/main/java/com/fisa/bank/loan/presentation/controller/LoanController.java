package com.fisa.bank.loan.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import com.fisa.bank.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.loan.application.dto.request.LoanMonthlyRepayRequest;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanApplyforResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductResponse;
import com.fisa.bank.loan.application.dto.response.PagedResponse;
import com.fisa.bank.loan.application.service.LoanService;
import com.fisa.bank.loan.persistence.entity.LoanProduct;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  // 은행
  @PostMapping
  public ApiResponse<SuccessBody<LoanProductCreateResponse>> createLoanProduct(
      @Valid @RequestBody LoanProductCreateRequest requestDTO) {

    LoanProductCreateResponse response = loanService.createLoanProduct(requestDTO);

    return ApiResponseGenerator.success(ResponseCode.CREATE, response);
  }

  @GetMapping("/products")
  public ApiResponse<SuccessBody<PagedResponse<LoanProductResponse<LoanProduct>>>> getLoanProducts(
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    PagedResponse<LoanProductResponse<LoanProduct>> allProducts =
        loanService.findAllProducts(pageable);

    return ApiResponseGenerator.success(ResponseCode.GET, allProducts);
  }

  @GetMapping("/{loanProductId}")
  public ApiResponse<SuccessBody<LoanProductResponse<LoanProduct>>> getLoanProductById(
      @PathVariable Long loanProductId) {
    LoanProductResponse<LoanProduct> loanProduct = loanService.findProductById(loanProductId);

    return ApiResponseGenerator.success(ResponseCode.GET, loanProduct);
  }

  @DeleteMapping("/products/{loanProductId}")
  public ApiResponse<SuccessBody<Void>> deleteLoanProduct(@PathVariable Long loanProductId) {

    loanService.deleteLoanProduct(loanProductId);

    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }

  // 사용자 - 대출 상품 가입, 대출 해지, 대출 상환, 대출 상환 내역 조회
  @PostMapping("/{loanProductId}")
  public ApiResponse<SuccessBody<LoanApplyforResponse>> applyForLoan(
      @PathVariable Long loanProductId, @Valid @RequestBody LoanApplyForRequest request) {

    LoanApplyforResponse loanApplyforResponse = loanService.applyForLoan(request, loanProductId);

    return ApiResponseGenerator.success(ResponseCode.CREATE, loanApplyforResponse);
  }

  // 대출 상환
  @PostMapping("/{loanLedgerId}/repayment")
  public void repayMonthlyLoan(
      @PathVariable Long loanLedgerId, @RequestBody LoanMonthlyRepayRequest request) {
    System.out.println("대출상환");
    loanService.repayMonthlyLoan(loanLedgerId, request);
  }
}
