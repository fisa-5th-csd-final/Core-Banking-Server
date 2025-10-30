package com.fisa.bank.loan.application.model;

import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.LoanType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 중도 상환 수수료 테이블 조회 테스트 코드
public class EarlyRepayInterestRateTest {

    // 신용 고정 0.02
    @Test
    void testCreditFixed() {
        BigDecimal fee = EarlyRepayInterestRate.getEarlyRepayInterestRate(LoanType.CREDIT, InterestType.FIXED);
        assertEquals(new BigDecimal("0.02"), fee);
    }

    // 신용 변동 0.02
    @Test
    void testCreditVariable() {
        BigDecimal fee = EarlyRepayInterestRate.getEarlyRepayInterestRate(LoanType.CREDIT, InterestType.VARIABLE);
        assertEquals(new BigDecimal("0.02"), fee);
    }

    // 담보 고정 0.73
    @Test
    void testMortgageFixed() {
        BigDecimal fee = EarlyRepayInterestRate.getEarlyRepayInterestRate(LoanType.MORTGAGE, InterestType.FIXED);
        assertEquals(new BigDecimal("0.73"), fee);
    }

    // 담보 변동 0.73
    @Test
    void testMortgageVariable() {
        BigDecimal fee = EarlyRepayInterestRate.getEarlyRepayInterestRate(LoanType.MORTGAGE, InterestType.VARIABLE);
        assertEquals(new BigDecimal("0.73"), fee);
    }
}
