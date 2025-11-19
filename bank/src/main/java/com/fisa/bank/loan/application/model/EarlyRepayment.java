package com.fisa.bank.loan.application.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import com.fisa.bank.loan.persistence.entity.LoanLedger;

/** 중도 상환할 경우, 납입 정보를 담는 객체 */
@Getter
public class EarlyRepayment {

  private final BigDecimal earlyPaidRate; // 상환 수수료율
  private final BigDecimal remainPrincipal; // 남은 원금
  private final BigDecimal earlyPaidCost; // 상환 수수료율 * 남은 잔금
  private final BigDecimal mustPaidAmount; // 내야 할 전체 금액

  /** 중도상환수수료 = 남은 원금 * 수수료율 * 잔존기간(일) / 대출기간(일) */
  private EarlyRepayment(
      BigDecimal remainPrincipal,
      BigDecimal earlyPaidRate,
      LocalDateTime loanStartedAt,
      LocalDateTime repaidAt,
      LocalDateTime loanEndAt) {
    this.remainPrincipal = remainPrincipal;
    this.earlyPaidRate = earlyPaidRate;
    this.earlyPaidCost =
        calculateCost(remainPrincipal, earlyPaidRate, loanStartedAt, repaidAt, loanEndAt);
    this.mustPaidAmount = remainPrincipal.add(earlyPaidCost);
  }

  private BigDecimal calculateCost(
      BigDecimal remain,
      BigDecimal rate,
      LocalDateTime loanStartedAt,
      LocalDateTime repaidAt,
      LocalDateTime loanEndAt) {
    ZoneId KST = ZoneId.of("Asia/Seoul");

    long totalDay = ChronoUnit.DAYS.between(loanStartedAt.toLocalDate(), loanEndAt.toLocalDate());
    long usingDay = ChronoUnit.DAYS.between(loanStartedAt.toLocalDate(), repaidAt.toLocalDate());
    long remaining = (totalDay - usingDay);

    if (totalDay <= 0) {
      throw new IllegalArgumentException("날짜 정보를 잘못 입력하였습니다.");
    }

    return remain
        .multiply(rate)
        .multiply(BigDecimal.valueOf(remaining))
        .divide(BigDecimal.valueOf(totalDay), 0, RoundingMode.HALF_UP);
  }

  public static EarlyRepayment create(LoanLedger loanLedger, LocalDateTime now, BigDecimal rate) {
    return new EarlyRepayment(
        loanLedger.getRemainPrincipal(),
        rate,
        loanLedger.getCreatedAt(),
        now,
        loanLedger.getLoanEndDate());
  }
}
