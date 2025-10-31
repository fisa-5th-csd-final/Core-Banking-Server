package com.fisa.bank.account.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;

public class AccessDeniedException extends BusinessException {
    private static final String ERROR_CODE = "A005";
    private static final String MESSAGE = "접근 권한이 없습니다";

    public AccessDeniedException() {
        super(ERROR_CODE, MESSAGE);
    }
}
