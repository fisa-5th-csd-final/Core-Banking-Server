package com.fisa.bank.loan.application.model;

import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.LoanType;
import com.fisa.bank.loan.persistence.enums.RepaymentType;

import java.math.BigDecimal;
import java.nio.channels.ReadPendingException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EarlyRepayInterestRate {
    private static final Map<LoanType,Map<InterestType, BigDecimal>> earlyRepayTable = new HashMap<>();

    static {
        // 신용 대출
        Map<InterestType, BigDecimal> credit = new EnumMap<>(InterestType.class);
        credit.put(InterestType.FIXED, new BigDecimal("0.02")); // 고정 금리
        credit.put(InterestType.VARIABLE, new BigDecimal("0.02")); // 변동 금리
        earlyRepayTable.put(LoanType.CREDIT, credit);

        // 담보 대출
        Map<InterestType, BigDecimal> mort = new EnumMap<>(InterestType.class);
        mort.put(InterestType.FIXED, new BigDecimal("0.73")); // 고정 금리
        mort.put(InterestType.VARIABLE, new BigDecimal("0.73")); // 변동 금리
        earlyRepayTable.put(LoanType.MORTGAGE, mort);
    }

    // 중도 상환 수수료 조회 메소드
    public static BigDecimal getEarlyRepayInterestRate(LoanType loanType, InterestType interestType){
        return earlyRepayTable.get(loanType).get(interestType);
    }
}
