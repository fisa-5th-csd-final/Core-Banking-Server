package com.fisa.bank.loan.persistence.repository;

import com.fisa.bank.loan.persistence.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanProduct, Long>{
}
