package com.fisa.bank.loan.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;

public class LoanLedgerIdJavaType extends BaseIdJavaType<Long, LoanLedgerId> {
  public static final LoanLedgerIdJavaType INSTANCE = new LoanLedgerIdJavaType();

  public LoanLedgerIdJavaType() {
    super(LoanLedgerId.class, LoanLedgerId::of, LongJavaType.INSTANCE, BigIntJdbcType.INSTANCE);
  }
}
