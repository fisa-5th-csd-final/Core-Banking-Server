package com.fisa.bank.interest.persistence.repository;

import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.interest.persistence.id.InterestRateId;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, InterestRateId> {
    Optional<InterestRate> findFirstByLoanProductOrderByCreatedAtDesc(LoanProduct loanProduct);
}
