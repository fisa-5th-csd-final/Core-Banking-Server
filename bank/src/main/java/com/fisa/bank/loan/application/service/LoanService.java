package com.fisa.bank.loan.application.service;

import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequestDTO;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponseDTO;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.enums.LoanType;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanProductCreateResponseDTO createLoanProduct(String name, LoanType type){

        LoanProduct loanProduct = loanRepository.save(LoanProduct.builder()
                .name(name)
                .type(type)
                .build());

        LoanProductCreateResponseDTO response = LoanProductCreateResponseDTO.builder()
                .name(name)
                .type(type)
                .loanProductId(loanProduct.getLoanProductId())
                .build();

        return response;
    }
}
