package com.fisa.bank.loan.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.user.persistence.entity.id.UserId;

public interface LoanLedgerRepository extends JpaRepository<LoanLedger, LoanLedgerId> {
  boolean existsByUser_UserIdAndLoanProduct_LoanProductId(
      UserId userId, LoanProductId loanProductId);

  List<LoanLedger> findAllByUser_UserId(UserId userId);
}
