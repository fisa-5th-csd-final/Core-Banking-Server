package com.fisa.bank.account.presentation.controller;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.request.TransferRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.dto.response.TransferResponse;
import com.fisa.bank.account.application.service.TransactionService;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * TransactionController
 *
 * 계좌 관련 거래(입금, 출금, 송금)를 처리하는 컨트롤러
 */
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

    // 입금
    @PostMapping("/{accountId}/deposit")
    public ApiResponse<SuccessBody<AccountTransactionResponse>> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDepositRequest request
    ) {
        AccountTransactionResponse response = transactionService.deposit(accountId, request);
        return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
    }

    // 송금
    @PostMapping("/transfer")
    public ApiResponse<SuccessBody<TransferResponse>> transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        TransferResponse response = transactionService.transfer(request);
        return ApiResponseGenerator.success(ResponseCode.UPDATE, response);
    }
}
