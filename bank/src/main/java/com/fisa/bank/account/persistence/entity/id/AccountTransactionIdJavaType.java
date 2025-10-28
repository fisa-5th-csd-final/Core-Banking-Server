package com.fisa.bank.account.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

/**
 * AccountTransactionIdJavaType
 *
 * Hibernate가 AccountTransactionId를 BigInt 컬럼으로 매핑하도록 하는 타입 변환기.
 */

public class AccountTransactionIdJavaType
        extends BaseIdJavaType<Long, AccountTransactionId> {

    public static final AccountTransactionIdJavaType INSTANCE = new AccountTransactionIdJavaType();

    private AccountTransactionIdJavaType() {
        super(
                AccountTransactionId.class,
                AccountTransactionId::of,
                LongJavaType.INSTANCE,
                BigIntJdbcType.INSTANCE
        );
    }
}
