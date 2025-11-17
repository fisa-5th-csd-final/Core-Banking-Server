package com.fisa.bank.domains.account.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.entity.AccountTransaction;
import com.fisa.bank.domains.account.persistence.entity.id.AccountTransactionId;

public interface AccountTransactionRepository
    extends JpaRepository<AccountTransaction, AccountTransactionId> {

  List<AccountTransaction> findByAccountAndCreatedAtGreaterThanEqualAndCreatedAtBefore(
      Account account, LocalDateTime start, LocalDateTime end);
}
