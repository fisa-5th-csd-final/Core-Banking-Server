package com.fisa.bank.loan.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;

public interface LoanRepository extends JpaRepository<LoanProduct, LoanProductId> {
  @Query(value = "SELECT * FROM loan_product WHERE loan_product_id = :id", nativeQuery = true)
  Optional<LoanProduct> findByIdIgnoringRestriction(@Param("id") Long id);
}
