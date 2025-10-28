package com.fisa.bank.user.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class InvalidAuthInfoException extends BusinessException {

    public static final InvalidAuthInfoException EXCEPTION = new InvalidAuthInfoException();

    private static final String errorCode = "1000";
    private static final String message = "이미 존재하는 ID 입니다.";

    private InvalidAuthInfoException() {
        super(errorCode, message);
    }

}
