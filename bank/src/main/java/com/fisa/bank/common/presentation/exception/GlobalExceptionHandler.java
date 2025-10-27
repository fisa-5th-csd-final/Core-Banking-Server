package com.fisa.bank.common.presentation.exception;

import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.FailureBody;
import com.fisa.bank.common.presentation.response.code.ApiResponseCode.ErrorResponseCode;
import com.fisa.bank.common.presentation.response.code.BusinessErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 애플리케이션 전역 Exception 핸들러
// BusinessException을 제외하고, 다른 종류의 예외들도 추가할 수 있다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 애플리케이션의 공통 Exception인 BusinessException을 캐치하는 Exception 핸들러
     * BusinessException에, 예외 정보에 대한 구조를 정해서, Http Response를 쉽게 작성할 수 있다.
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<FailureBody> handle(BusinessException e){
        // BusinessException 과 관련된 HttpStatus, ErrorCode, Message 를 가져오기
        ErrorResponseCode<BusinessException> errorCode = BusinessErrorCode.find(e);

        return ApiResponseGenerator.fail(errorCode);
    }

    /**
     * BusinessException 종류가 아닌, 모든 런타임 예외는 아래의 ExceptionHandler가 캐치한다.
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<FailureBody> handle(RuntimeException e){
        return ApiResponseGenerator.fail(HttpStatus.INTERNAL_SERVER_ERROR, "500", e.getMessage());
    }

}
