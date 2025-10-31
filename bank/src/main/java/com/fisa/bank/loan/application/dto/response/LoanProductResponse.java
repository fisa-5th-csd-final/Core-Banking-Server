package com.fisa.bank.loan.application.dto.response;

import com.fisa.bank.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.LoanType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LoanProductResponse<T> {
    private List<T> data;

    private final Long id;
    private final String name;
    private final LoanType type;
    private final InterestRateResponse interestRateResponse;

    public static LoanProductResponse<LoanProduct> from(LoanProduct entity, InterestRateResponse interestRateResponse){
        return new LoanProductResponse<LoanProduct>(
                entity.getLoanProductId().getValue(),
                entity.getName(),
                entity.getType(),
                interestRateResponse // 가장 최신 금리만 저장
        );
    }
    public LoanProductResponse(Long id, String name, LoanType type, InterestRateResponse interestRateResponse){
        this.id = id;
        this.name = name;
        this.type = type;
        this.interestRateResponse = interestRateResponse;
    }

}

