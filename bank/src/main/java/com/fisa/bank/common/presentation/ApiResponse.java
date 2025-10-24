package com.fisa.bank.common.presentation;

import com.fisa.bank.common.presentation.ApiResponse.Body;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<B extends Body<?>> extends ResponseEntity<B> {

    private final B body;

    public ApiResponse(HttpStatus statusCode) {
        super(statusCode);
        this.body = null;
    }

    public ApiResponse(HttpStatus statusCode, B body){
        super(statusCode);
        this.body = body;
    }

    // HttpResponse 의 Body에 담는 데이터를 표현한 추상 클래스
    public static abstract class Body<T> { }

    @Getter
    @RequiredArgsConstructor
    public static class SuccessBody<T> extends Body<T> {

        private final String code;
        private final String message;
        private final T data;

        public SuccessBody(MessageCode messageCode, T data){
            this.message = messageCode.getMessage();
            this.code = messageCode.getCode();
            this.data = data;
        }

    }

    @Getter
    @RequiredArgsConstructor
    public static class FailureBody<T> extends Body<T> {
        private final String errorCode;
        private final String message;

        // TODO: MessageCode 에는 요청 성공에 대한 응답 코드만 존재한다. 하지만 FailureCode 도 만들 것인가?
    }


}
