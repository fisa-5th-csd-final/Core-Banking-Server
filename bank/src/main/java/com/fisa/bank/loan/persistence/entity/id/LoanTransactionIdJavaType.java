package com.fisa.bank.loan.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class LoanTransactionIdJavaType extends BaseIdJavaType<Long, LoanTransactionId>{
    public static final LoanTransactionIdJavaType INSTANCE = new LoanTransactionIdJavaType();

    public LoanTransactionIdJavaType(){
        super(
                LoanTransactionId.class,
                LoanTransactionId::of,
                LongJavaType.INSTANCE,
                BigIntJdbcType.INSTANCE
        );
    }
}