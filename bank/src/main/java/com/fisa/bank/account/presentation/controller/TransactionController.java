package com.fisa.bank.account.presentation.controller;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.service.TransactionService;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    // 출금 API
    @PostMapping("/{accountId}/withdraw")
    public ApiResponse<SuccessBody<AccountTransactionResponse>> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountWithdrawRequest request
    ) {
        AccountTransactionResponse response = transactionService.withdraw(accountId, request);
        return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
    }

    @PostMapping("/{accountId}/deposit")
    public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDepositRequest request) {

        AccountTransactionResponse response =transactionService.deposit(accountId, request);
        return ApiResponseGenerator.success(ResponseCode.CREATE, response);
    }
}
