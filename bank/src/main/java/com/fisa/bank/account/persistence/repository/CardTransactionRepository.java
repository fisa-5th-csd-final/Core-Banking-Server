package com.fisa.bank.account.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.entity.id.CardTransactionId;

public interface CardTransactionRepository
    extends JpaRepository<CardTransaction, CardTransactionId> {}
