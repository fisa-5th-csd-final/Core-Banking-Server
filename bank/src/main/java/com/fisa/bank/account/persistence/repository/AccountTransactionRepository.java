package com.fisa.bank.account.persistence.repository;

import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.entity.id.AccountTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository
        extends JpaRepository<AccountTransaction, AccountTransactionId> {
}

