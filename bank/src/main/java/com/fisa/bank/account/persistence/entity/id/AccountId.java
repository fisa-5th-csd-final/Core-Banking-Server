package com.fisa.bank.account.persistence.entity.id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountId implements Serializable {

    private final Long value;

    public static AccountId of(Long value){
        if(value < 0) throw new IllegalArgumentException("AccountId value is not negative");
        return new AccountId(value);
    }
}
