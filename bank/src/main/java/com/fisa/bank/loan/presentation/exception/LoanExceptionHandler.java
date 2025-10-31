package com.fisa.bank.loan.presentation.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.FailureBody;

@RestControllerAdvice
@Slf4j
public class LoanExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<FailureBody> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    String message =
        errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));

    return ApiResponseGenerator.fail(HttpStatus.BAD_REQUEST, "400", message);
  }
}
