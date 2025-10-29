package com.fisa.bank.loan.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanTransactionId extends BaseId<Long> {
    private LoanTransactionId(Long value){ super(value); }

    public static LoanTransactionId of(Long value) { return new LoanTransactionId(value);};
}