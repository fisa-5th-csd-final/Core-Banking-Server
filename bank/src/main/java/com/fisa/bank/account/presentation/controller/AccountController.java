package com.fisa.bank.account.presentation.controller;

import com.fisa.bank.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.account.application.dto.response.AccountListResponse;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.service.AccountService;
import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final RequesterInfo requesterInfo;

  // 계좌 생성 API
  @PostMapping
  public ApiResponse<SuccessBody<AccountResponse>> createAccount() {
    Long userId = requesterInfo.getUserId().getValue();
    AccountResponse response = accountService.createAccount(userId);
    return ApiResponseGenerator.success(ResponseCode.CREATE, response);
  }

  // 계좌 상세 조회
  @GetMapping("/{accountNumber}")
  public ApiResponse<SuccessBody<AccountDetailResponse>> getAccountDetail(
      @PathVariable String accountNumber) {
    Long userId = requesterInfo.getUserId().getValue();
    AccountDetailResponse response = accountService.getAccountDetail(accountNumber, userId);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }

  // 계좌 리스트 조회
  @GetMapping
  public ApiResponse<SuccessBody<List<AccountListResponse>>> getAccountsByUserId() {
    Long userId = requesterInfo.getUserId().getValue();
    List<AccountListResponse> response = accountService.getAccountsByUserId(userId);
    return ApiResponseGenerator.success(ResponseCode.GET, response);
  }

  // 계좌 삭제
  @DeleteMapping("/{accountNumber}")
  public ApiResponse<SuccessBody<Void>> deleteAccount(@PathVariable String accountNumber) {
    Long userId = requesterInfo.getUserId().getValue();
    accountService.deleteAccount(accountNumber, userId);
    return ApiResponseGenerator.success(ResponseCode.DELETE);
  }
}
