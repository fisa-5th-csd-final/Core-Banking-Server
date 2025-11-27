package com.fisa.bank.domains.loan.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.account.application.service.AccountDomainRecorder;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.loan.application.model.MonthlyRepayment;
import com.fisa.bank.domains.loan.application.model.UpdateLoanLedgerParam;
import com.fisa.bank.domains.loan.application.service.calculator.CalculatorService;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;

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
      // 연체분을 포함한 월별 상환 목록(0번이 가장 오래된 회차)
      List<MonthlyRepayment> repayments = calculatorService.calculate(ledger);
      if (repayments.isEmpty()) {
        return;
      }

      Account loanAccount = ledger.getAccount();
      Account salaryAccount = null;

      for (MonthlyRepayment repayment : repayments) {

        BigDecimal requiredAmount = repayment.getMonthlyPayment();
        BigDecimal loanBalance = loanAccount.getBalance();

        // 대출계좌 잔액으로 충분한 경우
        if (loanBalance.compareTo(requiredAmount) >= 0) {
          withdraw(loanAccount, requiredAmount);
          handleRepaymentSuccess(ledger, repayment);
          continue;
        }

        // 잔액 부족한 경우: 부족분 계산
        BigDecimal shortage = requiredAmount.subtract(loanBalance);

        // 급여계좌 조회(한 번만 조회 후 재사용)
        if (salaryAccount == null) {
          salaryAccount =
              accountRepository.findFirstByUserAndIsForIncomeTrue(ledger.getUser()).orElse(null);
        }

        if (salaryAccount == null) {
          log.warn("급여계좌 없음. 연체 처리: userId={}", ledger.getUser().getUserId().getValue());
          handleOverdue(ledger);
          break;
        }

        BigDecimal salaryBalance = salaryAccount.getBalance();

        // 급여계좌로 부족분 충당 가능한 경우
        if (salaryBalance.compareTo(shortage) >= 0) {

          // 급여계좌에서 대출계좌로 부족분 이체
          transfer(salaryAccount, loanAccount, shortage);

          // 대출계좌에서 전체 출금
          withdraw(loanAccount, requiredAmount);

          handleRepaymentSuccess(ledger, repayment);
          continue;
        }

        // 급여계좌에도 돈 없는 경우: 연체 처리 후 종료
        handleOverdue(ledger);
        break;
      }

    } catch (Exception ex) {
      log.error(
          "[AutoDeposit Failed] ledgerId={}, cause={}",
          ledger.getLoanLedgerId().getValue(),
          ex.getMessage(),
          ex);
    }
  }

  // 출금
  private void withdraw(Account account, BigDecimal amount) {
    accountDomainRecorder.record(account, amount, TransactionType.TRANSFER_SEND, false, null);
  }

  // 계좌 간 이체
  private void transfer(Account from, Account to, BigDecimal amount) {
    accountDomainRecorder.record(
        from, amount, TransactionType.TRANSFER_SEND, false, to.getAccountNumber());

    accountDomainRecorder.record(
        to, amount, TransactionType.TRANSFER_RECEIVE, true, from.getAccountNumber());
  }

  // 상환 성공 시 남은 원금 감소, 다음 상환일 갱신
  private void handleRepaymentSuccess(LoanLedger ledger, MonthlyRepayment repayment) {

    BigDecimal newRemainPrincipal =
        ledger.getRemainPrincipal().subtract(repayment.getPrincipalPayment());

    LocalDate repaymentDate = repayment.getRepaymentDate().toLocalDate();
    LocalDate nextRepaymentDate = ledger.getNextRepaymentDate().toLocalDate();

    if (repaymentDate.isBefore(nextRepaymentDate)) {
      handleOverdueRepaymentSuccess(ledger, repayment, newRemainPrincipal);
      return;
    }

    handleNormalRepaymentSuccess(ledger, repayment, newRemainPrincipal);
  }

  // 연체 회차 상환 성공: 다음 상환일은 유지, 마지막 상환일은 해당 회차 상환일로 설정
  private void handleOverdueRepaymentSuccess(
      LoanLedger ledger, MonthlyRepayment repayment, BigDecimal newRemainPrincipal) {
    ledger.updateLoanLedger(
        new UpdateLoanLedgerParam(
            newRemainPrincipal,
            ledger.getNextRepaymentDate(),
            repayment.getRepaymentDate(),
            RepaymentStatus.OVERDUE));
  }

  // 정상 회차 상환 성공: 다음 상환일 +1개월, 마지막 상환일은 해당 회차 상환일로 설정
  private void handleNormalRepaymentSuccess(
      LoanLedger ledger, MonthlyRepayment repayment, BigDecimal newRemainPrincipal) {
    ledger.updateLoanLedger(
        new UpdateLoanLedgerParam(
            newRemainPrincipal,
            ledger.getNextRepaymentDate().plusMonths(1),
            repayment.getRepaymentDate(),
            RepaymentStatus.NORMAL));
  }

  // 연체 처리(OVERDUE 상태로 변경)
  private void handleOverdue(LoanLedger ledger) {
    boolean alreadyOverdue = ledger.getRepaymentStatus() == RepaymentStatus.OVERDUE;

    LocalDateTime nextRepaymentDate =
        alreadyOverdue
            ? ledger.getNextRepaymentDate()
            : ledger.getNextRepaymentDate().plusMonths(1);

    ledger.updateLoanLedger(
        new UpdateLoanLedgerParam(
            ledger.getRemainPrincipal(), // 상환 안했으니 원금 변화 없음
            nextRepaymentDate,
            ledger.getLastRepaymentDate(),
            RepaymentStatus.OVERDUE));
    ledger.increaseOverdueCount();
  }
}
