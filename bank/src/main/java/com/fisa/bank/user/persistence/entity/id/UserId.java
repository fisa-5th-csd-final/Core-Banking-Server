package com.fisa.bank.user.persistence.entity.id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UserId {

    private Long value;

    public static UserId of(Long value){
        if(value < 0) throw new IllegalArgumentException("UserId value is not negative");
        return new UserId(value);
    }

}
