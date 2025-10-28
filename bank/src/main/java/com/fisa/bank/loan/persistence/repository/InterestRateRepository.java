package com.fisa.bank.loan.persistence.repository;

import com.fisa.bank.loan.persistence.entity.InterestRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, Long> {
}
