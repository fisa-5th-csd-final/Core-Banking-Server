package com.fisa.bank.interest.persistence.repository;

import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.interest.persistence.id.InterestRateId;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, InterestRateId> {
    Optional<InterestRate> findFirstByLoanProductOrderByCreatedAtDesc(LoanProduct loanProduct);

    List<InterestRate> findAllByLoanProduct_LoanProductId(LoanProductId loanProductId);
}
