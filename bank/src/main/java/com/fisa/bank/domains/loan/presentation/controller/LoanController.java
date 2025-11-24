package com.fisa.bank.domains.loan.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisa.bank.domains.common.presentation.response.ApiResponse;
import com.fisa.bank.domains.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.domains.common.presentation.response.body.SuccessBody;
import com.fisa.bank.domains.common.presentation.response.code.ResponseCode;
import com.fisa.bank.domains.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.domains.loan.application.dto.request.LoanAutoDepositUpdateRequest;
import com.fisa.bank.domains.loan.application.dto.request.LoanMonthlyRepayRequest;
import com.fisa.bank.domains.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.domains.loan.application.dto.response.*;
import com.fisa.bank.domains.loan.application.service.AutoDepositService;
import com.fisa.bank.domains.loan.application.service.LoanService;

@Slf4j
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
  public ApiResponse<SuccessBody<PagedResponse<LoanProductResponse>>> getLoanProducts(
      @PageableDefault(page = 0, size = 10) Pageable pageable) {

    PagedResponse<LoanProductResponse> allProducts = loanService.getAllProducts(pageable);

    return ApiResponseGenerator.success(ResponseCode.GET, allProducts);
  }

  @GetMapping("/{loanProductId:\\d+}")
  public ApiResponse<SuccessBody<LoanProductResponse>> getLoanProductById(
      @PathVariable Long loanProductId) {
    LoanProductResponse loanProduct = loanService.getProductById(loanProductId);

    return ApiResponseGenerator.success(ResponseCode.GET, loanProduct);
  }

  @DeleteMapping("/products/{loanProductId:\\d+}")
  public ApiResponse<SuccessBody<Void>> deleteLoanProduct(@PathVariable Long loanProductId) {

    loanService.deleteLoanProduct(loanProductId);

    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }

  // 사용자 - 대출 상품 가입, 대출 해지, 대출 상환, 대출 상환 내역 조회
  @PostMapping("/{loanProductId:\\d+}")
  public ApiResponse<SuccessBody<LoanApplyforResponse>> applyForLoan(
      @PathVariable Long loanProductId, @Valid @RequestBody LoanApplyForRequest request) {
    log.info("대출 가입");
    LoanApplyforResponse loanApplyforResponse = loanService.applyForLoan(request, loanProductId);

    return ApiResponseGenerator.success(ResponseCode.CREATE, loanApplyforResponse);
  }

  // 대출 상환
  @PostMapping("/{loanLedgerId:\\d+}/repayment")
  public ApiResponse<SuccessBody<LoanTransactionResponse>> repayMonthlyLoan(
      @PathVariable Long loanLedgerId, @RequestBody LoanMonthlyRepayRequest request) {
    log.info("대출 상환");
    LoanTransactionResponse loanTransactionResponse =
        loanService.repayMonthlyLoan(loanLedgerId, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, loanTransactionResponse);
  }

  @DeleteMapping("/{loanLedgerId:\\d+}")
  public ApiResponse<SuccessBody<Void>> deleteLoanLedger(
      @PathVariable("loanLedgerId") Long loanLedgerId) {
    log.info("대출 해지");
    loanService.cancelLoan(loanLedgerId);
    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }

  @GetMapping("/ledgers")
  public ApiResponse<SuccessBody<List<LoanLedgerResponse>>> getMyLoanLedgers() {
    log.info("대출 리스트 조회");
    List<LoanLedgerResponse> myLoanLedger = loanService.getMyLoanLedgers();

    return ApiResponseGenerator.success(ResponseCode.GET, myLoanLedger);
  }

  @GetMapping("/ledger/{loanLedgerId:\\d+}")
  public ApiResponse<SuccessBody<LoanLedgerDetailResponse>> getLoanLedgerDetail(
      @PathVariable Long loanLedgerId) {
    log.info("대출 세부 정보 조회");
    LoanLedgerDetailResponse loanLedgerDetail = loanService.getLoanLedgerDetail(loanLedgerId);

    return ApiResponseGenerator.success(ResponseCode.GET, loanLedgerDetail);
  }

  @PatchMapping("/{loanLedgerId:\\d+}/auto-deposit")
  public ApiResponse<SuccessBody<Void>> updateAutoDepositEnabled(
      @PathVariable Long loanLedgerId, @Valid @RequestBody LoanAutoDepositUpdateRequest request) {
    log.info("자동예치 여부 수정");
    loanService.updateAutoDepositEnabled(loanLedgerId, request.getAutoDepositEnabled());

    return ApiResponseGenerator.success(ResponseCode.UPDATE);
  }

  @GetMapping("/prepayment-infos")
  public ApiResponse<SuccessBody<List<PrepaymentInfoResponse>>> getPrepaymentInfos() {
    log.info("선납 정보 조회");
    List<PrepaymentInfoResponse> prepaymentInfos = loanService.getPrepaymentInfos();
    return ApiResponseGenerator.success(ResponseCode.GET, prepaymentInfos);
  }

}
