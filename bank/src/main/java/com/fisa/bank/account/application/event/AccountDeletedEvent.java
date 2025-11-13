package com.fisa.bank.account.application.event;

import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.common.event.DomainEvent;

public class AccountDeletedEvent extends DomainEvent {

    public AccountDeletedEvent(AccountId id){
        super(id);
    }

}
