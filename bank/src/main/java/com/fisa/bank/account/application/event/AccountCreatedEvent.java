package com.fisa.bank.account.application.event;

import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.common.event.DomainEvent;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AccountCreatedEvent extends DomainEvent {

    private final Long userId;
    private final String accountNumber;
    private final BigDecimal balance;
    private final String bankCode;

    public AccountCreatedEvent(AccountId id, Long userId, String accountNumber, BigDecimal balance, String bankCode){
        super(id);
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.bankCode = bankCode;
    }

}
