package com.fisa.bank.domains.loan.application.service.reader;

import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.loan.application.exception.LoanLedgerNotFoundException;
import com.fisa.bank.domains.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.loan.persistence.entity.LoanProduct;
import com.fisa.bank.domains.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.domains.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.domains.loan.persistence.repository.LoanLedgerRepository;
import com.fisa.bank.domains.loan.persistence.repository.LoanRepository;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanReader {
  private final LoanRepository loanRepository;
  private final LoanLedgerRepository loanLedgerRepository;

  private static final List<RepaymentStatus> EXCLUDED_REPAYMENT_STATUSES =
      List.of(RepaymentStatus.COMPLETED, RepaymentStatus.TERMINATED);

  public Page<LoanProduct> findAllProducts(Pageable pageable) {
    return loanRepository.findAll(pageable);
  }

  public LoanProduct findProductById(Long loanProductId) {
    return loanRepository
        .findById(LoanProductId.of(loanProductId))
        .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));
  }

  public LoanLedger findLoanLedgerById(Long loanLedgerId) {

    return loanLedgerRepository
        .findByLoanLedgerIdAndRepaymentStatusNotIn(
            LoanLedgerId.of(loanLedgerId), EXCLUDED_REPAYMENT_STATUSES)
        .orElseThrow(() -> new LoanLedgerNotFoundException(loanLedgerId));
  }

  public List<LoanLedger> findAllByUserId(Long userId) {
    return loanLedgerRepository.findAllByUser_UserId(UserId.of(userId));
  }

  public boolean existsByUserIdAndLoanProductId(Long userId, Long loanProductId) {
    return loanLedgerRepository.existsByUser_UserIdAndLoanProduct_LoanProductId(
        UserId.of(userId), LoanProductId.of(loanProductId));
  }
}
