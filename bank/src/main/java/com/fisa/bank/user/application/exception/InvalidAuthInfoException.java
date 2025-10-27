package com.fisa.bank.user.application.exception;

public class InvalidAuthInfoException extends RuntimeException {

    private static final String message = "이미 존재하는 ID 입니다.";
    public InvalidAuthInfoException() {
        super(message);
    }
}
