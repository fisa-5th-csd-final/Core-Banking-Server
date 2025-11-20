package com.fisa.bank.domains.account.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
  // 상위 트랜잭션이 종료되어도, 무조건 새로운 트랜잭션을 연다.
  // 상위 트랜잭션은 이미 COMMIT 된 상태여서, 자원을 정리하고 있다.
  // accountService.createIncomeAccount() 메소드는 @Transactional 어노테이션으로
  // 기존에 상위 트랜잭션이 존재하면 해당 트랜잭션에 참여하려고 한다. 하지만 이미 커밋이 된 상태이고, 트랜잭션 매니저가 자원을 정리 중이므로
  // 제대로된 트랜잭션을 사용할 수 없다. 따라서 Propagation.REQUIRES_NEW 를 사용해서 무조건 새로운 트랜잭션을 열도록 한다.
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createIncomeAccount(UserCreatedEvent event) {
    // TODO: 재시도 로직 추가
    String accountNumber = accountService.createIncomeAccount(event.getUserId()).accountNumber();
    log.info("소득 계좌 생성 성공 : {}", accountNumber);
  }
}
