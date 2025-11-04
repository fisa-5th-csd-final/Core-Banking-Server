package com.fisa.bank.loan.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;

public interface LoanLedgerRepository extends JpaRepository<LoanLedger, LoanLedgerId> {}
