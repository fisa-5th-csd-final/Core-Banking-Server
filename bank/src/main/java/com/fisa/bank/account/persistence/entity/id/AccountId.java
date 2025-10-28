package com.fisa.bank.account.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseId;
import com.fisa.bank.user.persistence.entity.id.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountId extends BaseId<Long> {

    // 생성자
    private AccountId(Long value) {
        super(value);
    }

    // 팩토리 메서드
    public static AccountId of(Long value){
        return new AccountId(value);
    }
}
