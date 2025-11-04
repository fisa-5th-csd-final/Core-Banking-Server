package com.fisa.bank.account.application.dto.response;

import java.util.List;

/** 계좌 거래내역 전체 응답 DTO */
public record AccountTransactionListResponse(
    String accountNumber, // 계좌 ID
    List<AccountTransactionResponse> transactions // 거래내역 리스트
    ) {}
