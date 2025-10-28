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

    // specificValidate를 @Override 하지 않아도 됩니다.
    // BaseId의 기본 구현(No-op)이 사용됩니다.
    // (만약 UserId에 추가적인 규칙이 생긴다면 그때 @Override하여 구현하면 됩니다.)
}
