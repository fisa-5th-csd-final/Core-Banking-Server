package com.fisa.bank.account.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

/**
 * AccountNotFoundException
 *
 * 계좌가 존재하지 않을 때 발생하는 비즈니스 예외
 */

public class AccountNotFoundException extends BusinessException {

    public static final AccountNotFoundException EXCEPTION = new AccountNotFoundException();

    private static final String ERROR_CODE = "A001";
    private static final String MESSAGE = "계좌를 찾을 수 없습니다.";

    private AccountNotFoundException() {
        super(ERROR_CODE, MESSAGE);
    }
}
