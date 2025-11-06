package com.fisa.bank.loan.persistence.enums;

// 상환 상태 - 정상, 연체, 중도 상환, 만기 상환
public enum RepaymentStatus {
  NORMAL,
  OVERDUE,
    TERMINATED,
    COMPLETED;
}
