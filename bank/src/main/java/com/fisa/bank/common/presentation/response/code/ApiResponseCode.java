package com.fisa.bank.common.presentation.response.code;

import org.springframework.http.HttpStatus;

// TODO: HttpStatus 의존성 제거하기
public interface ApiResponseCode {

    public HttpStatus getStatus();
    public String getCode();
    public String getMessage();

}
