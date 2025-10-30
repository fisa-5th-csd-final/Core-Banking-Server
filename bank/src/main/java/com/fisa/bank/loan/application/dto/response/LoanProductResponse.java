package com.fisa.bank.loan.application.dto.response;

import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.LoanType;
import lombok.Getter;

import java.util.List;

@Getter
public class LoanProductResponse<T> {
    private List<T> data;

    private final LoanProductId id;
    private final String name;
    private final LoanType type;
//    private List<InterestRate> interestRateList;


    public static LoanProductResponse<LoanProduct> from(LoanProduct entity){
        return new LoanProductResponse<LoanProduct>(
                entity.getLoanProductId(),
                entity.getName(),
                entity.getType()
//                entity.getInterestRateList()
        );
    }
    public LoanProductResponse(LoanProductId id, String name, LoanType type){
        this.id = id;
        this.name = name;
        this.type = type;
//        this.interestRateList = interestRateList;
    }
}

