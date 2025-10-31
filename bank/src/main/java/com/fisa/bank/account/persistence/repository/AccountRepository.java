package com.fisa.bank.account.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.user.persistence.entity.User;

/** 계좌 엔티티 JPA Repository */
public interface AccountRepository extends JpaRepository<Account, AccountId> {
  boolean existsByAccountNumber(String accountNumber);

  List<Account> findAllByUser(User user);
}
