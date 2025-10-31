package com.fisa.bank.account.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.enums.ConsumptionCategory;

@Builder
public record CardPaymentResponse(
    Long trxCId,
    Long accountId,
    String storeName,
    BigDecimal amount,
    ConsumptionCategory category,
    BigDecimal balanceAfter,
    LocalDateTime transactionAt,
    String message) {
  public static CardPaymentResponse of(CardTransaction trx) {
    return CardPaymentResponse.builder()
        .trxCId(trx.getTrxCId().getValue())
        .accountId(trx.getAccount().getAccountId().getValue())
        .storeName(trx.getStoreName())
        .amount(trx.getAmount())
        .category(trx.getCategory())
        .balanceAfter(trx.getAccount().getBalance())
        .transactionAt(trx.getCreatedAt())
        .message("카드 결제가 성공적으로 처리되었습니다.")
        .build();
  }
}
