package com.fisa.bank.domains.loan.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.domains.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;

public interface LoanLedgerRepository extends JpaRepository<LoanLedger, LoanLedgerId> {
  boolean existsByUser_UserIdAndLoanProduct_LoanProductId(
      UserId userId, LoanProductId loanProductId);

  @EntityGraph(attributePaths = {"loanProduct", "account"})
  List<LoanLedger> findAllByUser_UserId(UserId userId);

  Optional<LoanLedger> findByLoanLedgerIdAndRepaymentStatusNotIn(
      LoanLedgerId loanLedgerId, List<RepaymentStatus> statuses);

  @Query(
      """
    SELECT l
      FROM LoanLedger l
     WHERE l.autoDepositEnabled = true
       AND FUNCTION('DATE', l.nextRepaymentDate) <= CURRENT_DATE
       AND l.repaymentStatus IN (
                           com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus.NORMAL,
                           com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus.OVERDUE
                      )
    """)
  List<LoanLedger> findAutoDepositTargets();
}
