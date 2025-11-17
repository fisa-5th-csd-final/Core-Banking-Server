package com.fisa.bank.domains.loan.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.domains.loan.persistence.entity.LoanTransaction;
import com.fisa.bank.domains.loan.persistence.entity.id.LoanTransactionId;

public interface LoanTransactionRepository
    extends JpaRepository<LoanTransaction, LoanTransactionId> {}
