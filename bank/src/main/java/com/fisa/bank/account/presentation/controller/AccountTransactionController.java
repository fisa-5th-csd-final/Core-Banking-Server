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
import com.fisa.bank.account.application.service.AccountTransactionService;
import com.fisa.bank.common.application.util.RequesterInfo;
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
public class AccountTransactionController {

  private final AccountTransactionService accountTransactionService;
  private final RequesterInfo requesterInfo;

  // 출금 API
  @PostMapping("/{accountNumber}/withdraw")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> withdraw(
      @PathVariable String accountNumber, @Valid @RequestBody AccountWithdrawRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionResponse response = accountTransactionService.withdraw(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 입금
  @PostMapping("/{accountNumber}/deposit")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
      @PathVariable String accountNumber, @Valid @RequestBody AccountDepositRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionResponse response = accountTransactionService.deposit(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 송금
  @PostMapping("/transfer")
  public ApiResponse<SuccessBody<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    TransferResponse response = accountTransactionService.transfer(request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 카드결제
  @PostMapping("/{accountNumber}/pay")
  public ApiResponse<SuccessBody<CardPaymentResponse>> pay(
      @PathVariable String accountNumber, @Valid @RequestBody CardPaymentRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    CardPaymentResponse response = accountTransactionService.payByCard(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  // 거래내역 조회
  @GetMapping("/{accountNumber}/transactions")
  public ApiResponse<SuccessBody<AccountTransactionListResponse>> getTransactions(
      @PathVariable String accountNumber,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionListResponse response =
        accountTransactionService.getTransactions(accountNumber, startDate, endDate, userId);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }
}
