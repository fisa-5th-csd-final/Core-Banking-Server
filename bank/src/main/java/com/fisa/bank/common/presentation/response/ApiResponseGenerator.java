package com.fisa.bank.common.presentation.response;

import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode;
import com.fisa.bank.common.presentation.response.code.ResponseCode;

public class ApiResponseGenerator {

    /**
     * 200 OK 응답을 만들고 싶을 때 사용하는 static 메소드
     * @param body
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<SuccessBody<T>> ok(T body){
        ApiResponseCode responseCode = ResponseCode.GET;

        return new ApiResponse<>(
                responseCode.getStatus(),
                new SuccessBody<>(responseCode.getCode(), responseCode.getMessage(), body)
        );
    }

    /**
     *
     * @param responseCode
     * @param body
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<SuccessBody<T>> create(ApiResponseCode responseCode, T body){

        String code = responseCode.getCode();
        String message = responseCode.getMessage();

        return new ApiResponse<>(
                responseCode.getStatus(),
                new SuccessBody<>(code, message, body)
        );
    }

}
