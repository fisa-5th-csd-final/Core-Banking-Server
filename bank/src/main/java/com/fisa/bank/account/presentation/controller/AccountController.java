package com.fisa.bank.account.presentation.controller;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.response.AccountBalanceResponse;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.service.AccountService;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 계좌 잔액 조회
    @GetMapping("/{accountId}/balance")
    public ApiResponse<SuccessBody<AccountBalanceResponse>> getAccountBalance(@PathVariable Long accountId) {
        AccountBalanceResponse response = accountService.getAccountBalance(accountId);
        return ApiResponseGenerator.success(ResponseCode.GET, response);
    }
}
