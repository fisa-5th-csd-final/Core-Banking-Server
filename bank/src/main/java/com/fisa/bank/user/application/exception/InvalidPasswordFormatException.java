package com.fisa.bank.user.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class InvalidPasswordFormatException extends BusinessException {

    public static final InvalidPasswordFormatException EXCEPTION = new InvalidPasswordFormatException();

    private static final String errorCode = "1001";
    private static final String message = "비밀번호 규칙에 맞지 않습니다.";

    private InvalidPasswordFormatException() {
        super(errorCode, message);
    }
}
