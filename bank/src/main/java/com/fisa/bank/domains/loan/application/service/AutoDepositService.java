package com.fisa.bank.domains.loan.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fisa.bank.domains.loan.application.service.reader.LoanReader;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoDepositService {

  private final LoanReader loanReader;
  private final AutoDepositProcessor processor;
  private final JdbcTemplate jdbc;

  // 중복 실행 방지용 LOCK 키
  private static final String LOCK_KEY = "AUTO_DEPOSIT_LOCK";

  // 자동예치 전체 처리 로직
  public void processAutoDeposits() {

    if (!acquireLock()) {
      log.warn("자동예치 작업이 이미 실행 중 → 중복 실행 방지됨");
      return;
    }

    try {
      List<LoanLedger> targets = loanReader.findAutoDepositTargets();

      log.info("자동예치 대상 {}건 처리 시작", targets.size());

      // 모든 ledger 가져와 하나씩 독립적으로 처리
      for (LoanLedger ledger : targets) {
        processor.processOne(ledger);
      }

      log.info("자동예치 작업 완료");

    } finally { // lock 해제
      releaseLock();
    }
  }

  // DB에 row 삽입: 성공 시 락 획득, 실패 시 중복 실행 방지
  private boolean acquireLock() {
    try {
      String sql =
          """
                    INSERT INTO batch_lock(lock_name, locked_at)
                    VALUES (?, NOW())
                    """;
      jdbc.update(sql, LOCK_KEY);
      return true;
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      return false;
    }
  }

  // 락 해제
  private void releaseLock() {
    jdbc.update("DELETE FROM batch_lock WHERE lock_name = ?", LOCK_KEY);
  }
}
