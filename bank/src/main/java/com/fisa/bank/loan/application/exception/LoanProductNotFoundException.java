package com.fisa.bank.loan.application.exception;

public class LoanProductNotFoundException extends LoanProductException {

    // 404 Not Found에 적합
    private static final String DEFAULT_MESSAGE = "요청하신 대출 상품을 찾을 수 없습니다.";

    public LoanProductNotFoundException(Long productId) {
        super(DEFAULT_MESSAGE + " (ID: " + productId + ")");
    }

    public LoanProductNotFoundException(String message) {
        super(message);
    }
}
