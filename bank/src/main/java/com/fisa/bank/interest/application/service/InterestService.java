package com.fisa.bank.interest.application.service;

import com.fisa.bank.interest.application.exception.InterestException;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.interest.persistence.repository.InterestRateRepository;
import com.fisa.bank.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.user.application.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

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

        // 대출 상품에 금리 추가
        loanProduct.getInterestRateList().add(savedInterestRate);

        return savedInterestRate;
    }

//    @Transactional
//    public Map<String, BigDecimal> findInterestRate(LoanProduct loanProduct){
//        InterestRate interestRate = interestRateRepository.findFirstByLoanProductOrderByCreatedAtDesc(loanProduct)
//                .orElseThrow(() -> new InterestException(loanProduct.getLoanProductId()));
//        Map<String, BigDecimal> interests = Map.of(
//                "addInterest", interestRate.getAddInterest(),
//                "baseInterest", interestRate.getBaseInterest(),
//                "limitPreferInterest", interestRate.getLimitPreferInterest()
//        );
//        return interests;
//    }

}
