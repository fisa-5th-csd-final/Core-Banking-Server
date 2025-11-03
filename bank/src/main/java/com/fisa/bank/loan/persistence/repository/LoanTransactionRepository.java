package com.fisa.bank.loan.persistence.repository;

import com.fisa.bank.loan.persistence.entity.LoanTransaction;
import com.fisa.bank.loan.persistence.entity.id.LoanTransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, LoanTransactionId> {
}
