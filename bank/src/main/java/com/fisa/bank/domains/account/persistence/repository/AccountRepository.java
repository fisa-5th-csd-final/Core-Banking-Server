package com.fisa.bank.domains.account.persistence.repository;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.entity.id.AccountId;
import com.fisa.bank.domains.user.persistence.entity.User;

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

  // 송금 시 두 계좌를 한번에 처리하여 동시성 제어
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
                select a
                from Account a
                where a.accountNumber in :numbers
                order by a.accountNumber asc
            """)
  List<Account> lockTwoAccountsByNumbers(@Param("numbers") List<String> numbers);
}
