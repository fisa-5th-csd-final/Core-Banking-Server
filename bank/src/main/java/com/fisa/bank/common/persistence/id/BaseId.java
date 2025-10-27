package com.fisa.bank.common.persistence.id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class BaseId<T> {

    private final T value;

    // 외부에서 인스턴스화 불가하게 처리
    protected BaseId(T value) {
        if (value == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "null 값 예외 발생");
        }

        this.value = value;
    }
}