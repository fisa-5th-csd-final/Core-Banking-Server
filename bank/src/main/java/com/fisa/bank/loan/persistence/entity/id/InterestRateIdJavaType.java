package com.fisa.bank.loan.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;

public class InterestRateIdJavaType extends BaseIdJavaType<Long, InterestRateId> {
  public static final InterestRateIdJavaType INSTANCE = new InterestRateIdJavaType();

  public InterestRateIdJavaType() {
    super(InterestRateId.class, InterestRateId::of, LongJavaType.INSTANCE, BigIntJdbcType.INSTANCE);
  }
}
