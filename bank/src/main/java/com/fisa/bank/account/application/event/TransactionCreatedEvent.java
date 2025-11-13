package com.fisa.bank.account.application.event;

import com.fisa.bank.account.persistence.entity.id.AccountTransactionId;
import com.fisa.bank.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class TransactionCreatedEvent extends DomainEvent {

    public TransactionCreatedEvent(AccountTransactionId id) {
        super(id);
    }

}
