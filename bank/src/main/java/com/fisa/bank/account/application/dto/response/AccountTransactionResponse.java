package com.fisa.bank.account.application.dto.response;

import com.fisa.bank.account.persistence.entity.AccountTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 단일 거래내역 응답 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionResponse {

    private String trxId;             // 거래 ID
    private LocalDateTime date;       // 거래 일시
    private String type;              // 거래 유형 (DEPOSIT, WITHDRAWAL 등)
    private BigDecimal amount;        // 거래 금액
    private BigDecimal balanceAfter;  // 거래 후 잔액

    public static AccountTransactionResponse of(AccountTransaction entity) {
        return AccountTransactionResponse.builder()
                .trxId(entity.getTrxAId().getValue().toString())
                .date(entity.getDate())
                .type(entity.getType().name())
                .amount(entity.getAmount())
                .balanceAfter(entity.getBalanceAfter())
                .build();
    }
}
