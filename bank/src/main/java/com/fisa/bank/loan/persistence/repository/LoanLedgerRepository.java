package com.fisa.bank.loan.persistence.repository;

import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanLedgerRepository extends JpaRepository<LoanLedger, LoanLedgerId> {
}
