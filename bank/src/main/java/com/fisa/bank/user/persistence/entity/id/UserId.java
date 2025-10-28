package com.fisa.bank.user.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserId extends BaseId<Long> {

    private UserId(Long value) {
        super(value);
    }

    public static UserId of(Long value){
        return new UserId(value);
    }
}
