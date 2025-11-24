package com.fisa.bank.domains.loan.application.service;

import com.fisa.bank.domains.loan.application.service.reader.LoanReader;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoDepositService {

    private final LoanReader loanReader;
    private final AutoDepositProcessor processor;
    private final JdbcTemplate jdbc;

    // 중복 실행 방지용 LOCK 키
    private static final String LOCK_KEY = "AUTO_DEPOSIT_LOCK";

    public void processAutoDeposits() {

        if (!acquireLock()) {
            log.warn("자동예치 작업이 이미 실행 중 → 중복 실행 방지됨");
            return;
        }

        try {
            List<LoanLedger> targets = loanReader.findAutoDepositTargets();

            log.info("자동예치 대상 {}건 처리 시작", targets.size());

            for (LoanLedger ledger : targets) {
                processor.processOne(ledger);
            }

            log.info("자동예치 작업 완료");

        } finally {
            releaseLock();
        }
    }

    /** DB 기반 락 획득 */
    private boolean acquireLock() {
        try {
            String sql = """
                    INSERT INTO batch_lock(lock_name, locked_at)
                    VALUES (?, NOW())
                    """;
            jdbc.update(sql, LOCK_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 락 해제 */
    private void releaseLock() {
        jdbc.update("DELETE FROM batch_lock WHERE lock_name = ?", LOCK_KEY);
    }
}
