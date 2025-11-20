package com.fisa.bank.domains.account.application.service;

import com.fisa.bank.domains.account.application.util.AccountNumberGenerator;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountCreator {

  @Value("${bank.code}")
  private String ourBankCode;

  private final AccountNumberGenerator accountNumberGenerator;
  private final UserRepository userRepository;

  public Account createIncomeAccount(User user) {
    String accountNumber = accountNumberGenerator.generateUnique();
    return Account.create(accountNumber, user, ourBankCode, true);
  }

  public Account createNonIncomeAccount(User user) {
    String accountNumber = accountNumberGenerator.generateUnique();
    return Account.create(accountNumber, user, ourBankCode, false);
  }

}
