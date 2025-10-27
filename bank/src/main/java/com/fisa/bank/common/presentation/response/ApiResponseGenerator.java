package com.fisa.bank.common.presentation.response;

import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode;
import com.fisa.bank.common.presentation.response.code.MessageCode;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import org.springframework.http.HttpStatus;

public class ApiResponseGenerator {

    /**
     *
     * @param responseCode
     * @param body
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<SuccessBody<T>> success(ApiResponseCode responseCode, T body){

        String code = responseCode.getCode();
        String message = responseCode.getMessage();

        return new ApiResponse<>(
                responseCode.getStatus(),
                new SuccessBody<>(code, message, body)
        );
    }

    public static <T> ApiResponse<SuccessBody<T>> success(ApiResponseCode responseCode){
        String code = responseCode.getCode();
        String message = responseCode.getMessage();

        return new ApiResponse<>(
                responseCode.getStatus(),
                new SuccessBody<>(code, message, null)
        );
    }

    public static <T> ApiResponse<SuccessBody<T>> success (HttpStatus status, MessageCode messageCode, T body){
        String code = messageCode.getCode();
        String message = messageCode.getMessage();
        return new ApiResponse<>(status, new SuccessBody<>(code, message, body));
    }

}
