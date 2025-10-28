package com.fisa.bank.account.persistence.repository;

import com.fisa.bank.account.persistence.entity.AccountTransactionEntity;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.entity.id.AccountTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository
        extends JpaRepository<AccountTransactionEntity, AccountTransactionId> {
}

