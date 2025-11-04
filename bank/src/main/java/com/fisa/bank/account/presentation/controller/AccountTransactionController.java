package com.fisa.bank.account.presentation.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * TransactionController
 *
 * <p>계좌 관련 거래(입금, 출금, 송금)를 처리하는 컨트롤러
 */
@Tag(name = "Account Transaction", description = "계좌 거래 API")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountTransactionController {

  private final AccountTransactionService accountTransactionService;
  private final RequesterInfo requesterInfo;

  @Operation(summary = "출금", description = "계좌에서 금액을 출금합니다.")
  @PostMapping("/{accountNumber}/withdraw")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> withdraw(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber,
      @Valid @RequestBody AccountWithdrawRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionResponse response = accountTransactionService.withdraw(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "입금", description = "계좌에 금액을 입금합니다.")
  @PostMapping("/{accountNumber}/deposit")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber,
      @Valid @RequestBody AccountDepositRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionResponse response = accountTransactionService.deposit(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "송금", description = "다른 계좌로 금액을 송금합니다.")
  @PostMapping("/transfer")
  public ApiResponse<SuccessBody<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    TransferResponse response = accountTransactionService.transfer(request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "카드 결제", description = "계좌를 통해 카드 결제를 진행합니다.")
  @PostMapping("/{accountNumber}/pay")
  public ApiResponse<SuccessBody<CardPaymentResponse>> pay(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber,
      @Valid @RequestBody CardPaymentRequest request) {
    Long userId = requesterInfo.getUserId().getValue();
    CardPaymentResponse response = accountTransactionService.payByCard(accountNumber, request, userId);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "거래내역 조회", description = "특정 기간의 계좌 거래내역을 조회합니다.")
  @GetMapping("/{accountNumber}/transactions")
  public ApiResponse<SuccessBody<AccountTransactionListResponse>> getTransactions(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber,
      @Parameter(description = "조회 시작일 (YYYY-MM-DD)", required = true) @RequestParam LocalDate startDate,
      @Parameter(description = "조회 종료일 (YYYY-MM-DD)", required = true) @RequestParam LocalDate endDate) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountTransactionListResponse response =
        accountTransactionService.getTransactions(accountNumber, startDate, endDate, userId);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }
}
