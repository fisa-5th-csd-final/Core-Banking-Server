package com.fisa.bank.loan.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;

public interface LoanRepository extends JpaRepository<LoanProduct, LoanProductId> {}
