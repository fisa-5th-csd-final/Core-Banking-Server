package com.fisa.bank.loan.persistence.repository;

import com.fisa.bank.loan.persistence.entity.PreferInterest;
import com.fisa.bank.loan.persistence.entity.PreferInterestCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferInterestRepository extends JpaRepository<PreferInterest, PreferInterestCompositeKey> {
}
