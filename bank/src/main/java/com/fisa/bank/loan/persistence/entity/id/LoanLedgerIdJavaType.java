package com.fisa.bank.loan.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class LoanLedgerIdJavaType extends BaseIdJavaType<Long, LoanLedgerId>{
    public static final LoanLedgerIdJavaType INSTANCE = new LoanLedgerIdJavaType();

    public LoanLedgerIdJavaType(){
        super(
                LoanLedgerId.class,
                LoanLedgerId::of,
                LongJavaType.INSTANCE,
                BigIntJdbcType.INSTANCE
        );
    }
}