package com.fisa.bank.domains.account.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.domains.account.persistence.entity.CardTransaction;
import com.fisa.bank.domains.account.persistence.entity.id.CardTransactionId;

public interface CardTransactionRepository
    extends JpaRepository<CardTransaction, CardTransactionId> {}
