package com.fisa.bank.common.presentation.response.code;


import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode.ErrorResponseCode;
import com.fisa.bank.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.user.application.exception.InvalidPasswordFormatException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCode implements ErrorResponseCode<BusinessException> {

    /**
     * 여기에 커스텀 BusinessException을 정의하면 됩니다.
     */

    INVALID_PASSWORD_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidPasswordFormatException.EXCEPTION),
    INVALID_AUTH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidAuthInfoException.EXCEPTION);

    private final HttpStatus status;
    @Getter  private final BusinessException exception;

    <T extends BusinessException> BusinessErrorCode(HttpStatus status, T exception){
        this.status = status;
        this.exception = exception;
    }


    @Override
    public HttpStatus getStatus() { return this.status; }

    @Override
    public String getCode() { return exception.getErrorCode(); }

    @Override
    public String getMessage() { return exception.getMessage(); }

    public static ErrorResponseCode<BusinessException> find(BusinessException exception){
        BusinessErrorCode errorResponseCode =  map.get(exception.getClass());

        if(errorResponseCode!=null) return errorResponseCode;

        throw new IllegalArgumentException("Not mapped Exception");
    }

    private static final Map<Class<? extends BusinessException>, BusinessErrorCode> map = new ConcurrentHashMap<>();

    static {
        Arrays.stream(BusinessErrorCode.values())
                .forEach(errorCode -> {
                    Class<? extends BusinessException> eClass = errorCode.exception.getClass();
                    map.put(eClass, errorCode);
                });
    }
}
