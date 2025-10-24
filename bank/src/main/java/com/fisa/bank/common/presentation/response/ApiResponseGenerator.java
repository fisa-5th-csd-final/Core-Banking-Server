package com.fisa.bank.common.presentation.response;

import com.fisa.bank.common.presentation.MessageCode;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import org.springframework.http.HttpStatus;

public class ApiResponseGenerator {

    /**
     * 200 OK 응답을 만들고 싶을 때 사용하는 static 메소드
     * @param body
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<SuccessBody<T>> ok(T body){
        return new ApiResponse<>(
                HttpStatus.OK,
                new SuccessBody<>("200", "", body)
        );
    }

    /**
     * status, messageCode, body를 받아서 응답을 생성하는 static 메소드
     * @param status
     * @param messageCode
     * @param body
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<SuccessBody<T>> create(HttpStatus status, MessageCode messageCode, T body){

        String code = messageCode.getCode();
        String message = messageCode.getMessage();

        return new ApiResponse<>(
                status,
                new SuccessBody<>(code, message, body)
        );
    }

}
