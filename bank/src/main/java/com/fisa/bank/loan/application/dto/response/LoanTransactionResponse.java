package com.fisa.bank.loan.application.dto.response;

import com.fisa.bank.loan.persistence.entity.LoanTransaction;
import com.fisa.bank.loan.persistence.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
public class LoanTransactionResponse {
    private final Long trxLId;
    private final LocalDateTime date;
    private final TransactionType transactionType; //대출, 상환, 연체 이자 납부

    // 대출일 경우 대출금, 상환일 경우 원금 + 이자, 연체 이자 납부일 경우 이자
    private final BigDecimal amount;
    private final BigDecimal remainPrincipal;

    // 상환, 연체 이자 납부일 경우 사용하는 필드
    private BigDecimal repaymentInterestAmount;
    private BigDecimal repaymentPrincipalAmount;

    public static LoanTransactionResponse from(LoanTransaction entity){
        return new LoanTransactionResponse(
                entity.getTrxLId().getValue(),
                entity.getDate(),
                entity.getTransactionType(),
                entity.getAmount(),
                entity.getRemainPrincipal()
        );
    }
    public LoanTransactionResponse(Long trxLId, LocalDateTime date, TransactionType transactionType, BigDecimal amount, BigDecimal remainPrincipal) {
        this.trxLId = trxLId;
        this.date = date;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remainPrincipal = remainPrincipal;
    }
}
