package com.fisa.bank.account.persistence.repository;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.entity.id.AccountTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountTransactionRepository
        extends JpaRepository<AccountTransaction, AccountTransactionId> {

    List<AccountTransaction> findByAccountAndDateGreaterThanEqualAndDateBefore(Account account, LocalDateTime start, LocalDateTime end);
}

