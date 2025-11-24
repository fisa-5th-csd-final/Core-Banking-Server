package com.fisa.bank.domains.loan.application.service;

import com.fisa.bank.domains.account.application.service.AccountDomainRecorder;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.application.model.UpdateLoanLedgerParam;
import com.fisa.bank.domains.loan.application.service.calculator.CalculatorService;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoDepositProcessor {

    private final AccountRepository accountRepository;
    private final AccountDomainRecorder accountDomainRecorder;
    private final CalculatorService calculatorService;

    // ledger 한 건 단위 트랜잭션
    @Transactional
    public void processOne(LoanLedger ledger) {

        try {
            // 이번 달 필요한 총 상환 금액 = 원금 + 이자
            MonthlyRepayment repayment = calculatorService.calculate(ledger);
            BigDecimal requiredAmount = repayment.getMonthlyPayment();

            Account loanAccount = ledger.getAccount();
            BigDecimal loanBalance = loanAccount.getBalance();

            // 대출계좌 잔액으로 충분한 경우
            if (loanBalance.compareTo(requiredAmount) >= 0) {
                withdraw(loanAccount, requiredAmount);
                handleRepaymentSuccess(ledger, repayment);
                return;
            }

            // 잔액 부족한 경우: 부족분 계산
            BigDecimal shortage = requiredAmount.subtract(loanBalance);

            // 급여계좌 조회
            Account salaryAccount = accountRepository
                    .findAllByUser(ledger.getUser()).stream()
                    .filter(Account::isForIncome)
                    .findFirst()
                    .orElse(null);

            // 급여계좌는 항상 있다고 가정
//            if (salaryAccount == null) {
//                log.warn("급여계좌 없음. 연체 처리: userId={}", ledger.getUser().getUserId().getValue());
//                handleOverdue(ledger);
//                return;
//            }

            BigDecimal salaryBalance = salaryAccount.getBalance();

            // 급여계좌로 부족분 충당 가능한 경우
            if (salaryBalance.compareTo(shortage) >= 0) {

                // 급여계좌에서 대출계좌로 부족분 이체
                transfer(salaryAccount, loanAccount, shortage);

                // 대출계좌에서 전체 출금
                withdraw(loanAccount, requiredAmount);

                handleRepaymentSuccess(ledger, repayment);
                return;
            }

            // 급여계좌에도 돈 없는 경우: 연체
            handleOverdue(ledger);

        } catch (Exception ex) {
            log.error("[AutoDeposit Failed] ledgerId={}, cause={}",
                    ledger.getLoanLedgerId().getValue(),
                    ex.getMessage(), ex);
        }
    }

    // 출금
    private void withdraw(Account account, BigDecimal amount) {
        accountDomainRecorder.record(
                account,
                amount,
                TransactionType.TRANSFER_SEND,
                false,
                null
        );
    }

    // 계좌 간 이체
    private void transfer(Account from, Account to, BigDecimal amount) {
        accountDomainRecorder.record(
                from,
                amount,
                TransactionType.TRANSFER_SEND,
                false,
                to.getAccountNumber()
        );

        accountDomainRecorder.record(
                to,
                amount,
                TransactionType.TRANSFER_RECEIVE,
                true,
                from.getAccountNumber()
        );
    }

    // 상환 성공 시 남은 원금 감소, 다음 상환일 갱신
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

    // 연체 처리(OVERDUE 상태로 변경)
    private void handleOverdue(LoanLedger ledger) {
        ledger.updateLoanLedger(
                new UpdateLoanLedgerParam(
                        ledger.getRemainPrincipal(), // 상환 안했으니 원금 변화 없음
                        ledger.getNextRepaymentDate().plusMonths(1), // 상환일 다음 달로 똑같이 넘어감
                        ledger.getLastRepaymentDate(),
                        RepaymentStatus.OVERDUE
                )
        );
        ledger.increaseOverdueCount();
    }
}

