package com.fisa.bank.domains.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisa.bank.domains.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.domains.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.domains.account.application.dto.request.CardPaymentRequest;
import com.fisa.bank.domains.account.application.dto.request.TransferRequest;
import com.fisa.bank.domains.account.application.dto.response.AccountListResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.domains.account.application.dto.response.CardPaymentResponse;
import com.fisa.bank.domains.account.application.dto.response.TransferResponse;
import com.fisa.bank.domains.account.application.service.AccountService;
import com.fisa.bank.domains.account.application.service.AccountTransactionService;
import com.fisa.bank.domains.admin.application.dto.AdminUserSummaryResponse;
import com.fisa.bank.domains.admin.application.service.AdminUserService;
import com.fisa.bank.domains.common.presentation.response.ApiResponse;
import com.fisa.bank.domains.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.domains.common.presentation.response.body.SuccessBody;
import com.fisa.bank.domains.common.presentation.response.code.ResponseCode;
import com.fisa.bank.domains.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.domains.loan.application.dto.request.LoanMonthlyRepayRequest;
import com.fisa.bank.domains.loan.application.dto.response.LoanApplyforResponse;
import com.fisa.bank.domains.loan.application.dto.response.LoanLedgerResponse;
import com.fisa.bank.domains.loan.application.dto.response.LoanTransactionResponse;
import com.fisa.bank.domains.loan.application.service.LoanService;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AccountService accountService;
  private final AccountTransactionService accountTransactionService;
  private final LoanService loanService;
  private final AdminUserService adminUserService;

  @Operation(summary = "관리자: 사용자 목록", description = "모든 사용자 목록을 조회합니다.")
  @GetMapping("/users")
  public ApiResponse<SuccessBody<List<AdminUserSummaryResponse>>> getUsers() {
    List<AdminUserSummaryResponse> users = adminUserService.getUsers();
    return ApiResponseGenerator.success(ResponseCode.GET, users);
  }

  @Operation(summary = "관리자: 계좌 생성", description = "특정 사용자의 계좌를 생성합니다. income=true면 급여계좌.")
  @PostMapping("/users/{userId}/accounts")
  public ApiResponse<SuccessBody<AccountResponse>> createAccountForUser(
      @PathVariable Long userId, @RequestParam(defaultValue = "false") boolean income) {
    AccountResponse response =
        income
            ? accountService.createIncomeAccount(userId)
            : accountService.createAccount(userId);
    return ApiResponseGenerator.success(ResponseCode.CREATE, response);
  }

  @Operation(summary = "관리자: 사용자 계좌 목록", description = "특정 사용자의 모든 계좌를 조회합니다.")
  @GetMapping("/users/{userId}/accounts")
  public ApiResponse<SuccessBody<List<AccountListResponse>>> getUserAccounts(
      @PathVariable Long userId) {
    List<AccountListResponse> accounts = accountService.getAccountsByUserId(userId);
    return ApiResponseGenerator.success(ResponseCode.GET, accounts);
  }

  @Operation(summary = "관리자: 계좌 삭제", description = "특정 계좌를 삭제합니다.")
  @DeleteMapping("/accounts/{accountNumber}")
  public ApiResponse<SuccessBody<Void>> deleteAccount(@PathVariable String accountNumber) {
    accountService.deleteAccount(accountNumber);
    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }

  @Operation(summary = "관리자: 출금", description = "특정 계좌에서 금액을 출금합니다.")
  @PostMapping("/accounts/{accountNumber}/withdraw")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> withdraw(
      @PathVariable String accountNumber, @Valid @RequestBody AccountWithdrawRequest request) {
    AccountTransactionResponse response = accountTransactionService.withdraw(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "관리자: 입금", description = "특정 계좌에 금액을 입금합니다.")
  @PostMapping("/accounts/{accountNumber}/deposit")
  public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
      @PathVariable String accountNumber, @Valid @RequestBody AccountDepositRequest request) {
    AccountTransactionResponse response = accountTransactionService.deposit(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "관리자: 송금", description = "특정 계좌에서 다른 계좌로 송금합니다.")
  @PostMapping("/accounts/transfer")
  public ApiResponse<SuccessBody<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request) {
    TransferResponse response = accountTransactionService.transfer(request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "관리자: 카드 결제", description = "특정 계좌로 카드 결제를 처리합니다.")
  @PostMapping("/accounts/{accountNumber}/pay")
  public ApiResponse<SuccessBody<CardPaymentResponse>> pay(
      @PathVariable String accountNumber, @Valid @RequestBody CardPaymentRequest request) {
    CardPaymentResponse response = accountTransactionService.payByCard(accountNumber, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "관리자: 대출 가입", description = "특정 사용자를 지정해 대출을 가입합니다.")
  @PostMapping("/users/{userId}/loans/{loanProductId}")
  public ApiResponse<SuccessBody<LoanApplyforResponse>> applyForLoan(
      @PathVariable Long userId,
      @PathVariable Long loanProductId,
      @Valid @RequestBody LoanApplyForRequest request) {
    LoanApplyforResponse response = loanService.applyForLoanAsAdmin(userId, request, loanProductId);
    return ApiResponseGenerator.success(ResponseCode.CREATE, response);
  }

  @Operation(summary = "관리자: 사용자 대출 목록", description = "특정 사용자의 대출 원장 목록을 조회합니다.")
  @GetMapping("/users/{userId}/loans")
  public ApiResponse<SuccessBody<List<LoanLedgerResponse>>> getLoanLedgers(
      @PathVariable Long userId) {
    List<LoanLedgerResponse> ledgers = loanService.getLoanLedgersByUser(userId);
    return ApiResponseGenerator.success(ResponseCode.GET, ledgers);
  }

  @Operation(summary = "관리자: 대출 상환", description = "특정 대출을 상환 처리합니다.")
  @PostMapping("/loans/{loanLedgerId}/repayment")
  public ApiResponse<SuccessBody<LoanTransactionResponse>> repayLoan(
      @PathVariable Long loanLedgerId, @RequestBody LoanMonthlyRepayRequest request) {
    LoanTransactionResponse response = loanService.repayMonthlyLoan(loanLedgerId, request);
    return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
  }

  @Operation(summary = "관리자: 대출 해지", description = "특정 대출을 해지합니다.")
  @DeleteMapping("/loans/{loanLedgerId}")
  public ApiResponse<SuccessBody<Void>> cancelLoan(@PathVariable Long loanLedgerId) {
    loanService.cancelLoan(loanLedgerId);
    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }
}
