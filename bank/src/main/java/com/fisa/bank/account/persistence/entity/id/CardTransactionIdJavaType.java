package com.fisa.bank.account.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class CardTransactionIdJavaType extends BaseIdJavaType<Long, CardTransactionId> {

    private CardTransactionIdJavaType() {
        super(
                CardTransactionId.class,
                CardTransactionId::of,
                LongJavaType.INSTANCE,
                BigIntJdbcType.INSTANCE
        );
    }
}
