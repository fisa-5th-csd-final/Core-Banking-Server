package com.fisa.bank.interest.application.service;

import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.interest.persistence.repository.InterestRateRepository;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final InterestRateRepository interestRateRepository;

    // 기준 금리 : COFIX 기준금리가 변하면 수정해야 해서 final 안 붙였습니다.
    private static BigDecimal baseInterest = new BigDecimal("2.5");

    @Transactional
    public InterestRate createInterestRate(LoanProduct loanProduct, BigDecimal addInterest, BigDecimal limitPreferInterest){

        InterestRate interestRate = InterestRate.builder()
                .loanProduct(loanProduct)
                .addInterest(addInterest)
                .limitPreferInterest(limitPreferInterest)
                .baseInterest(baseInterest)
                .build();

        InterestRate savedInterestRate = interestRateRepository.save(interestRate);

        return savedInterestRate;
    }

}
