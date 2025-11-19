package com.fisa.bank.domains.account.application.listener;

import com.fisa.bank.domains.account.application.service.AccountService;
import com.fisa.bank.domains.user.application.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

    private final AccountService accountService;

    @TransactionalEventListener(classes = UserCreatedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void createIncomeAccount(UserCreatedEvent event){
        // TODO: 재시도 로직 추가
        accountService.createAccount(event.getUserId());
    }

}
