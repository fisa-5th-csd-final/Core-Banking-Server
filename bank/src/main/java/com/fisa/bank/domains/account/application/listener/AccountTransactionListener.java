package com.fisa.bank.domains.account.application.listener;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fisa.bank.domains.account.application.service.AccountDomainRecoder;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.loan.application.event.LoanCancelledEvent;
import com.fisa.bank.domains.loan.application.event.LoanRepaidEvent;

@Component
@RequiredArgsConstructor
public class AccountTransactionListener {

  private final AccountDomainRecoder accountDomainRecoder;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleLoanRepaidEvent(LoanRepaidEvent event) {
    Account account = event.getAccount();

    // Manager에게 “출금 및 거래기록 생성” 위임
    accountDomainRecoder.record(
        account, event.getAmount(), TransactionType.LOAN_REPAYMENT, false, event.getLoanName());
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleLoanCancelledEvent(LoanCancelledEvent event) {
    Account account = event.getAccount();
    // Manager에게 “중도 상환 출금 및 거래기록 생성” 위임
    accountDomainRecoder.record(
        account, event.getAmount(), TransactionType.EARLY_REPAYMENT, false, event.getLoanName());
  }
}
