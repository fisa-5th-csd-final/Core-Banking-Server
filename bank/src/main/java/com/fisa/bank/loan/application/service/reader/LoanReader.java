package com.fisa.bank.loan.application.service.reader;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.loan.application.exception.LoanLedgerNotFoundException;
import com.fisa.bank.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.repository.LoanLedgerRepository;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import com.fisa.bank.user.persistence.entity.id.UserId;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanReader {
  private final LoanRepository loanRepository;
  private final LoanLedgerRepository loanLedgerRepository;

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
        .findById(LoanLedgerId.of(loanLedgerId))
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
