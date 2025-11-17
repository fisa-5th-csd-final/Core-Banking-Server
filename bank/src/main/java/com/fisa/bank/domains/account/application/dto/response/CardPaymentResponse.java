package com.fisa.bank.domains.account.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fisa.bank.domains.account.persistence.entity.CardTransaction;
import com.fisa.bank.domains.account.persistence.enums.ConsumptionCategory;

public record CardPaymentResponse(
    Long trxCId,
    String storeName,
    BigDecimal amount,
    ConsumptionCategory category,
    BigDecimal balanceAfter,
    LocalDateTime transactionAt,
    String message) {

  public static CardPaymentResponse from(CardTransaction trx) {
    return new CardPaymentResponse(
        trx.getTrxCId().getValue(),
        trx.getStoreName(),
        trx.getAmount(),
        trx.getCategory(),
        trx.getAccount().getBalance(),
        trx.getCreatedAt(),
        "카드 결제가 성공적으로 처리되었습니다.");
  }
}
