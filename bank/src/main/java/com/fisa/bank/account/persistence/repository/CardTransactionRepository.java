package com.fisa.bank.account.persistence.repository;

import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.entity.id.CardTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, CardTransactionId> {
}
