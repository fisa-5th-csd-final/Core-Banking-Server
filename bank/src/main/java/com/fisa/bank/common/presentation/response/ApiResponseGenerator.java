package com.fisa.bank.common.presentation.response;

import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode;
import com.fisa.bank.common.presentation.response.code.ResponseCode;

public class ApiResponseGenerator {

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
