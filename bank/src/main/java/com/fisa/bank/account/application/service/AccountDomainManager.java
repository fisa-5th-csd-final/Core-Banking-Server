package com.fisa.bank.account.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.account.persistence.repository.AccountTransactionRepository;

/*
    Account 도메인 이외에서는 주입하지 말 것.
*/
@Service
@RequiredArgsConstructor
public class AccountDomainManager {
  private final AccountTransactionRepository transactionRepository;

  @Transactional
  public AccountTransaction record(
      Account account,
      BigDecimal amount,
      TransactionType type,
      boolean isDeposit,
      String destination) {

    BigDecimal before = account.getBalance();

    if (isDeposit) {
      account.deposit(amount);
    } else {
      account.withdraw(amount);
    }

    BigDecimal after = account.getBalance();

    AccountTransaction trx =
        AccountTransaction.builder()
            .account(account)
            .type(type)
            .amount(amount)
            .balanceBefore(before)
            .balanceAfter(after)
            .isIncome(isDeposit)
            .destinationAccount(destination)
            .build();

    return transactionRepository.save(trx);
  }
}
