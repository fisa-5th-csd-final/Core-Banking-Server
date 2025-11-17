package com.fisa.bank.domains.loan.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.domains.common.persistence.id.BaseIdJavaType;

public class LoanTransactionIdJavaType extends BaseIdJavaType<Long, LoanTransactionId> {
  public static final LoanTransactionIdJavaType INSTANCE = new LoanTransactionIdJavaType();

  public LoanTransactionIdJavaType() {
    super(
        LoanTransactionId.class,
        LoanTransactionId::of,
        LongJavaType.INSTANCE,
        BigIntJdbcType.INSTANCE);
  }
}
