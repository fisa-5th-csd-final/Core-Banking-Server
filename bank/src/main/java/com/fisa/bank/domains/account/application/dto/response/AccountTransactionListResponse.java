package com.fisa.bank.domains.account.application.dto.response;

import java.util.List;

/** 계좌 거래내역 전체 응답 DTO */
public record AccountTransactionListResponse(
    Long accountId, // 계좌 ID
    String accountNumber, // 계좌 번호
    List<AccountTransactionResponse> transactions // 거래내역 리스트
    ) {}
