package com.fisa.bank.domains.account.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fisa.bank.domains.account.application.service.AccountService;
import com.fisa.bank.domains.user.application.event.UserCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

  private final AccountService accountService;

  @TransactionalEventListener(
      classes = UserCreatedEvent.class,
      phase = TransactionPhase.AFTER_COMMIT)
  public void createIncomeAccount(UserCreatedEvent event) {
    // TODO: 재시도 로직 추가
    String accountNumber = accountService.createIncomeAccount(event.getUserId()).accountNumber();
    log.info("소득 계좌 생성 성공 : {}", accountNumber);
  }
}
