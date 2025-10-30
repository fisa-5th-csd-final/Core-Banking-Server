package com.fisa.bank.interest.persistence.repository;

import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.interest.persistence.id.InterestRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, InterestRateId> {
}
