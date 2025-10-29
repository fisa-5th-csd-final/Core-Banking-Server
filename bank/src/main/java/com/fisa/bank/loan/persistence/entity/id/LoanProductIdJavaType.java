package com.fisa.bank.loan.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class LoanProductIdJavaType extends BaseIdJavaType<Long, LoanProductId>{
    public static final LoanProductIdJavaType INSTANCE = new LoanProductIdJavaType();

    public LoanProductIdJavaType(){
        super(
                LoanProductId.class,
                LoanProductId::of,
                LongJavaType.INSTANCE,
                BigIntJdbcType.INSTANCE
        );
    }
}
