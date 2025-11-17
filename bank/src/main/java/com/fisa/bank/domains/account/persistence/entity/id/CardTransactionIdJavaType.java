package com.fisa.bank.domains.account.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.domains.common.persistence.id.BaseIdJavaType;

public class CardTransactionIdJavaType extends BaseIdJavaType<Long, CardTransactionId> {
  public static final CardTransactionIdJavaType INSTANCE = new CardTransactionIdJavaType();

  private CardTransactionIdJavaType() {
    super(
        CardTransactionId.class,
        CardTransactionId::of,
        LongJavaType.INSTANCE,
        BigIntJdbcType.INSTANCE);
  }
}
