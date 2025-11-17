package com.fisa.bank.domains.account.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.fisa.bank.domains.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountListResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountResponse;
import com.fisa.bank.domains.account.application.service.AccountService;
import com.fisa.bank.domains.common.application.util.RequesterInfo;
import com.fisa.bank.domains.common.presentation.response.ApiResponse;
import com.fisa.bank.domains.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.domains.common.presentation.response.body.SuccessBody;
import com.fisa.bank.domains.common.presentation.response.code.ResponseCode;

@Tag(name = "Account", description = "계좌 관리 API")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final RequesterInfo requesterInfo;

  @Operation(summary = "계좌 생성", description = "새로운 계좌를 생성합니다.")
  @PostMapping
  public ApiResponse<SuccessBody<AccountResponse>> createAccount() {
    Long userId = requesterInfo.getUserId().getValue();
    AccountResponse response = accountService.createAccount(userId);
    return ApiResponseGenerator.success(ResponseCode.CREATE, response);
  }

  @Operation(summary = "계좌 상세 조회", description = "계좌 번호로 계좌의 상세 정보를 조회합니다.")
  @GetMapping("/{accountNumber}")
  public ApiResponse<SuccessBody<AccountDetailResponse>> getAccountDetail(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber) {
    AccountDetailResponse response = accountService.getAccountDetail(accountNumber);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }

  @Operation(summary = "계좌 목록 조회", description = "사용자의 모든 계좌 목록을 조회합니다.")
  @GetMapping
  public ApiResponse<SuccessBody<List<AccountListResponse>>> getAccountsByUserId() {
    Long userId = requesterInfo.getUserId().getValue();
    List<AccountListResponse> response = accountService.getAccountsByUserId(userId);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }

  @Operation(summary = "계좌 삭제", description = "계좌를 삭제합니다.")
  @DeleteMapping("/{accountNumber}")
  public ApiResponse<SuccessBody<Void>> deleteAccount(
      @Parameter(description = "계좌 번호", required = true) @PathVariable String accountNumber) {
    accountService.deleteAccount(accountNumber);
    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }
}
