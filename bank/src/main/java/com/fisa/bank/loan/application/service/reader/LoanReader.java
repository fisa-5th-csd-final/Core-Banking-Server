package com.fisa.bank.loan.application.service.reader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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

  @PersistenceContext private final EntityManager entityManager;

  public Page<LoanProduct> findAllProducts(Pageable pageable) {
    return loanRepository.findAll(pageable);
  }

  public LoanProduct findProductById(Long loanProductId) {
    return loanRepository
        .findById(LoanProductId.of(loanProductId))
        .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));
  }

  //  @ReadDeleted
  public LoanLedger findLoanLedgerById(Long loanLedgerId) {
    Session session = entityManager.unwrap(Session.class);

    Filter deletedFilter = session.enableFilter("deletedFilter");
    deletedFilter.setParameter("isDeleted", false);

    Filter ldeletedFilter = session.enableFilter("ldeletedFilter");
    ldeletedFilter.setParameter("isDeleted", false);
    // soft delete된 데이터 제외하는 필터 잠깐 중단
    //      session.disableFilter("deletedFilter");

    System.out.println("loanLedgerId 왜 이럼?: " + loanLedgerId);
    LoanLedger loanLedger =
        loanLedgerRepository
            .findById(LoanLedgerId.of(loanLedgerId))
            .orElseThrow(() -> new LoanLedgerNotFoundException(loanLedgerId));

    Hibernate.initialize(loanLedger.getLoanProduct());
    System.out.println("------");

    return loanLedger;
  }

  //  @ReadUnDeleted
  public List<LoanLedger> findAllByUserId(Long userId) {
    return loanLedgerRepository.findAllByUser_UserId(UserId.of(userId));
  }

  public boolean existsByUserIdAndLoanProductId(Long userId, Long loanProductId) {
    return loanLedgerRepository.existsByUser_UserIdAndLoanProduct_LoanProductId(
        UserId.of(userId), LoanProductId.of(loanProductId));
  }
}
