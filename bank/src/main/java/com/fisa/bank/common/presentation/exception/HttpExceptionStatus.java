package com.fisa.bank.common.presentation.exception;

import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.user.application.exception.InvalidPasswordFormatException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum HttpExceptionStatus {

    // TODO: BusinessException에 대한 Http status 는 어디서 관리해야 할까?
    // TODO: presentation, application ?

    INVALID_AUTH_INFO_EXCEPTION(400, InvalidAuthInfoException.class),
    INVALID_PASSWORD_FORMAT_EXCEPTION(400, InvalidPasswordFormatException.class);

    private int status;
    private Class<? extends BusinessException> exceptionClass;

    HttpExceptionStatus(int status, Class<? extends BusinessException> exceptionClass){
        this.status = status;
        this.exceptionClass = exceptionClass;
    }


    public static int getStatus(Class<? extends BusinessException> exceptionClass) {
        return Arrays.stream(HttpExceptionStatus.values())
                .filter(status -> status.getExceptionClass().equals(exceptionClass))
                .map(HttpExceptionStatus::getStatus)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found Exception Class"));
    }
}
