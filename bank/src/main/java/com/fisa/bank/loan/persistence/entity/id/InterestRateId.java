package com.fisa.bank.loan.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterestRateId extends BaseId<Long> {
    private InterestRateId(Long value){ super(value); }

    public static InterestRateId of(Long value) { return new InterestRateId(value);};
}