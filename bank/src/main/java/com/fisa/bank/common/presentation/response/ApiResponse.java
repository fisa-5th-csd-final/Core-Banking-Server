package com.fisa.bank.common.presentation.response;

import com.fisa.bank.common.presentation.response.body.ApiResponseBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<B extends ApiResponseBody> extends ResponseEntity<B> {

    private final B body;

    public ApiResponse(HttpStatus statusCode) {
        super(statusCode);
        this.body = null;
    }

    public ApiResponse(HttpStatus statusCode, B body){
        super(statusCode);
        this.body = body;
    }


}
