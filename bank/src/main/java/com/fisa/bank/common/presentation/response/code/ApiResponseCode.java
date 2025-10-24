package com.fisa.bank.common.presentation.response.code;

import org.springframework.http.HttpStatus;

public interface ApiResponseCode {

    public HttpStatus getStatus();
    public String getCode();
    public String getMessage();

}
