package com.fisa.bank.account.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountTransactionId extends BaseId<Long> {

    private AccountTransactionId(Long value) {
        super(value);
    }

    public static AccountTransactionId of(Long value) {
        return new AccountTransactionId(value);
    }
}
