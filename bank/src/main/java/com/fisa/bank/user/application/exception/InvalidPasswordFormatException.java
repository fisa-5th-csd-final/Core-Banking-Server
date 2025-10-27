package com.fisa.bank.user.application.exception;

public class InvalidPasswordFormatException extends RuntimeException {

    private static final String message = "비밀번호 규칙에 맞지 않습니다.";

    public InvalidPasswordFormatException() {
        super(message);
    }
}
