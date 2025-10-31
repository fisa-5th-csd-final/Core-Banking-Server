package com.fisa.bank.loan.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fisa.bank.loan.persistence.entity.InterestRate;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, Long> {}
