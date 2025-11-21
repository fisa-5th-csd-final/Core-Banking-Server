package com.fisa.bank.domains.loan.application.service;

import com.fisa.bank.domains.account.application.service.AccountDomainRecorder;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.application.model.UpdateLoanLedgerParam;
import com.fisa.bank.domains.loan.application.service.calculator.CalculatorService;
import com.fisa.bank.domains.loan.application.service.reader.LoanReader;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoDepositService {

    private static final Logger log = LoggerFactory.getLogger(AutoDepositService.class);
    private final LoanReader loanReader;
    private final AccountRepository accountRepository;
    private final AccountDomainRecorder accountDomainRecorder;
    private final CalculatorService calculatorService;

    @Transactional
    public void processAutoDeposits() {
        List<LoanLedger> ledgers = loanReader.findAutoDepositTargets();
        for (LoanLedger ledger : ledgers) {
            processOne(ledger);
        }
    }

    // ledger 한 건 처리하다 오류 나면 그 ledger만 롤백
    @Transactional
    public void processOne(LoanLedger ledger) {

        try {
            // 계산기로 이번 회사 상환금 계산
            MonthlyRepayment repayment = calculatorService.calculate(ledger);
            long requiredAmount = repayment.getMonthlyPayment().longValue();

            Account loanAccount = ledger.getAccount();
            BigDecimal loanBalance = loanAccount.getBalance();

            // 대출 계좌 잔액으로 충분할 경우
            if (loanBalance.compareTo(BigDecimal.valueOf(requiredAmount)) >= 0) {
                accountDomainRecorder.record( // 출금 기록
                        loanAccount,
                        BigDecimal.valueOf(requiredAmount),
                        TransactionType.TRANSFER_SEND,
                        false,
                        null
                );

                handleRepaymentSuccess(ledger, repayment);
                return;
            }

            // 대출 계좌 잔액 부족 시 부족분 계산
            long shortage = requiredAmount - loanBalance.longValue();

            // 급여 계좌 조회
            Account salaryAccount = accountRepository.findAllByUser(ledger.getUser()).stream()
                    .filter(Account::isForIncome) // 급여계좌
                    .findFirst()
                    .orElse(null);

            // 급여 계좌는 있다고 가정
//            if (salaryAccount == null) {
//                handleOverdue(ledger);
//                return;
//            }

            BigDecimal salaryBalance = salaryAccount.getBalance();

            // 급여 계좌로 부족분 충당 가능한지
            if (salaryBalance.compareTo(BigDecimal.valueOf(shortage)) >= 0) {

                // 부족분 충당
                accountDomainRecorder.record(
                        salaryAccount,
                        BigDecimal.valueOf(shortage),
                        TransactionType.TRANSFER_SEND,
                        false,
                        loanAccount.getAccountNumber()
                );

                accountDomainRecorder.record(
                        loanAccount,
                        BigDecimal.valueOf(shortage),
                        TransactionType.TRANSFER_RECEIVE,
                        true,
                        salaryAccount.getAccountNumber()
                );

                // 전체 상환 진행
                accountDomainRecorder.record(
                        loanAccount,
                        BigDecimal.valueOf(requiredAmount),
                        TransactionType.TRANSFER_SEND,
                        false,
                        null
                );

                handleRepaymentSuccess(ledger, repayment);
                return;
            }

            // 그래도 부족 → 연체
            handleOverdue(ledger);

        } catch (Exception e) {
            log.error(
                    "[AutoDeposit Failed] ledgerId={}, userId={}, cause={}",
                    ledger.getLoanLedgerId().getValue(),
                    ledger.getUser().getUserId().getValue(),
                    e.getMessage(),
                    e
            );
        }

    }

    /** 상환 성공 처리 */
    private void handleRepaymentSuccess(LoanLedger ledger, MonthlyRepayment repayment) {

        BigDecimal newRemainPrincipal =
                ledger.getRemainPrincipal().subtract(repayment.getPrincipalPayment());

        ledger.updateLoanLedger(
                new UpdateLoanLedgerParam(
                        newRemainPrincipal,
                        ledger.getNextRepaymentDate().plusMonths(1),
                        LocalDateTime.now(),
                        RepaymentStatus.NORMAL
                )
        );
    }

    /** 연체 처리 */
    private void handleOverdue(LoanLedger ledger) {
        ledger.updateLoanLedger(
                new UpdateLoanLedgerParam(
                        ledger.getRemainPrincipal(),
                        ledger.getNextRepaymentDate(),
                        ledger.getLastRepaymentDate(),
                        RepaymentStatus.OVERDUE
                )
        );
        ledger.increaseOverdueCount();
    }
}
