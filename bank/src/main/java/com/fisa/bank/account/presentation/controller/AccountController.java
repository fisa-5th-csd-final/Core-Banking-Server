package com.fisa.bank.account.presentation.controller;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.account.application.dto.response.AccountListResponse;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.service.AccountService;
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

    // 계좌 생성 API
    @PostMapping
    public ApiResponse<SuccessBody<AccountResponse>> createAccount(@RequestBody AccountCreateRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ApiResponseGenerator.success(ResponseCode.CREATE, response);
    }

    // 계좌 상세 조회
    @GetMapping("/{accountId}")
    public ApiResponse<SuccessBody<AccountDetailResponse>> getAccountDetail(@PathVariable Long accountId) {
        AccountDetailResponse response = accountService.getAccountDetail(accountId);
        return ApiResponseGenerator.success(ResponseCode.GET, response);
    }

    // 계좌 리스트 조회
    @GetMapping
    public ApiResponse<SuccessBody<List<AccountListResponse>>> getAccountsByUserId(@RequestParam Long userId) {
        List<AccountListResponse> response = accountService.getAccountsByUserId(userId);
        return ApiResponseGenerator.success(ResponseCode.GET, response);
    }
}
