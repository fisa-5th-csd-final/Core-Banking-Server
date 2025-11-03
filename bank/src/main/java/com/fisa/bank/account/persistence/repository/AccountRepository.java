package com.fisa.bank.account.persistence.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.user.persistence.entity.User;

/** 계좌 엔티티 JPA Repository */
public interface AccountRepository extends JpaRepository<Account, AccountId> {
  boolean existsByAccountNumber(String accountNumber);

  List<Account> findAllByUser(User user);

  // 계좌번호 조회
  Optional<Account> findByAccountNumber(String accountNumber);

  // 계좌번호 조회 (비관적 락)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
  Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

}
