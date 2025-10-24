package com.fisa.bank.user.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    private Long value;

    public static UserId of(Long value){
        if(value < 0) throw new IllegalArgumentException("UserId value is not negative");
        return new UserId(value);
    }

}
