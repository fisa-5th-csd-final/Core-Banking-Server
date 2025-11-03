package com.fisa.bank.loan.application.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MonthlyRepayment {
    private int term;
    private int remainingTerm; // 남은 상환 기간(월)
    private BigDecimal principalPayment;
    private BigDecimal interestPayment;
    private BigDecimal monthlyPayment;
    private boolean isLastMonth;

    public MonthlyRepayment(int term, BigDecimal principalPayment, BigDecimal interestPayment, BigDecimal monthlyPayment) {
        this.term = term;
        this.remainingTerm = term;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
//            this.totalPayment = principalPayment.add(interestPayment);
        this.monthlyPayment = monthlyPayment;
    }

    public MonthlyRepayment() {
    }
}
