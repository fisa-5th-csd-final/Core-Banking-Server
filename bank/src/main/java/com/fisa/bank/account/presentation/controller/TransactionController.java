package com.fisa.bank.account.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.request.CardPaymentRequest;
import com.fisa.bank.account.application.dto.request.TransferRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionListResponse;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.dto.response.CardPaymentResponse;
import com.fisa.bank.account.application.dto.response.TransferResponse;
import com.fisa.bank.account.application.service.TransactionService;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;

/**
 * TransactionController
 *
 * <p>계좌 관련 거래(입금, 출금, 송금)를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  // 출금 API
  @PostMapping("/{accountNumber}/withdraw")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> withdraw(
      @PathVariable String accountNumber, @Valid @RequestBody AccountWithdrawRequest request) {
    AccountTransactionResponse response = transactionService.withdraw(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 입금
  @PostMapping("/{accountNumber}/deposit")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
      @PathVariable String accountNumber, @Valid @RequestBody AccountDepositRequest request) {
    AccountTransactionResponse response = transactionService.deposit(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 송금
  @PostMapping("/transfer")
  public ApiResponse<SuccessBody<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request) {
    TransferResponse response = transactionService.transfer(request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 카드결제
  @PostMapping("/{accountNumber}/pay")
  public ApiResponse<SuccessBody<CardPaymentResponse>> pay(
      @PathVariable String accountNumber, @Valid @RequestBody CardPaymentRequest request) {
    CardPaymentResponse response = transactionService.payByCard(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 거래내역 조회
  @GetMapping("/{accountNumber}/transactions")
  public ApiResponse<SuccessBody<AccountTransactionListResponse>> getTransactions(
      @PathVariable String accountNumber,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate) {
    AccountTransactionListResponse response =
        transactionService.getTransactions(accountNumber, startDate, endDate);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }
}
