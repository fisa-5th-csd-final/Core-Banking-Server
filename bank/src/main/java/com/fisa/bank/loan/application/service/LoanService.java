package com.fisa.bank.loan.application.service;

import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;

    @org.springframework.transaction.annotation.Transactional
    public LoanProductCreateResponse createLoanProduct(LoanProductCreateRequest requestDTO){

        LoanProduct loanProduct = loanRepository.save(LoanProduct.builder()
                .name(requestDTO.getName())
                .type(requestDTO.getType())
                .build());

        LoanProductCreateResponse response = LoanProductCreateResponse.builder()
                .name(loanProduct.getName())
                .type(loanProduct.getType())
                .loanProductId(loanProduct.getLoanProductId())
                .build();

        return response;
    }
}
