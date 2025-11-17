package com.fisa.bank.domains.loan.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.domains.common.persistence.id.BaseIdJavaType;

public class LoanLedgerIdJavaType extends BaseIdJavaType<Long, LoanLedgerId> {
  public static final LoanLedgerIdJavaType INSTANCE = new LoanLedgerIdJavaType();

  public LoanLedgerIdJavaType() {
    super(LoanLedgerId.class, LoanLedgerId::of, LongJavaType.INSTANCE, BigIntJdbcType.INSTANCE);
  }
}
