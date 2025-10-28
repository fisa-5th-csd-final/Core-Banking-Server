package com.fisa.bank.account.persistence.repository;

import com.fisa.bank.account.persistence.entity.AccountEntity;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 계좌 엔티티 JPA Repository
 */
public interface AccountRepository extends JpaRepository<AccountEntity, AccountId> {
    boolean existsByAccountNumber(String accountNumber);
}
