package com.fisa.bank.common.presentation.response.code;


import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode.ErrorResponseCode;
import com.fisa.bank.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.user.application.exception.InvalidPasswordFormatException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fisa.bank.user.application.exception.UserNotFoundException;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCode implements ErrorResponseCode<BusinessException> {

    /**
     * 여기에 커스텀 BusinessException을 정의하면 됩니다.
     */

    INVALID_PASSWORD_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidPasswordFormatException.class),
    INVALID_AUTH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidAuthInfoException.class),
    ACCOUNT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, AccountNotFoundException .class),
    INSUFFICIENT_BALANCE_EXCEPTION(HttpStatus.BAD_REQUEST, InsufficientBalanceException .class),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, UserNotFoundException.class);

    private final HttpStatus status;
    @Getter private final Class<? extends BusinessException> exception;

    BusinessErrorCode(HttpStatus status, Class<? extends BusinessException> eClass){
        this.status = status;
        this.exception = eClass;
    }

    @Override
    public HttpStatus getStatus() { return this.status; }

    @Override
    public String getCode() { throw new UnsupportedOperationException("You should use errorCode, message in the BusinessException"); }
    @Override
    public String getMessage() { throw new UnsupportedOperationException("You should use errorCode, message in the BusinessException"); }

    public static ErrorResponseCode<BusinessException> find(BusinessException exception){
        BusinessErrorCode errorResponseCode =  map.get(exception.getClass());

        if(errorResponseCode!=null) return errorResponseCode;

        throw new IllegalArgumentException("Not mapped Exception");
    }

    /**
     * 커스텀 Exception에 맞는 ErrorCode를 매핑하는 저장소
     */
    private static final Map<Class<? extends BusinessException>, BusinessErrorCode> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(BusinessErrorCode.values())
                .forEach(errorCode -> {
                    Class<? extends BusinessException> eClass = errorCode.exception;
                    map.put(eClass, errorCode);
                });
    }
}
