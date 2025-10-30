package com.fisa.bank.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 계좌 거래내역 전체 응답 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionListResponse {

    private Long accountId; // 계좌 ID
    private List<AccountTransactionResponse> transactions; // 거래내역 리스트
}
