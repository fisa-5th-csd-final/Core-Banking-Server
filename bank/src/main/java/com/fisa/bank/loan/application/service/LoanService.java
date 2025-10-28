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

    @org.springframework.transaction.annotation.Transactional
    public LoanProductCreateResponseDTO createLoanProduct(LoanProductCreateRequestDTO requestDTO){

        LoanProduct loanProduct = loanRepository.save(LoanProduct.builder()
                .name(requestDTO.getName())
                .type(requestDTO.getType())
                .build());

        LoanProductCreateResponseDTO response = LoanProductCreateResponseDTO.builder()
                .name(loanProduct.getName())
                .type(loanProduct.getType())
                .loanProductId(loanProduct.getLoanProductId())
                .build();

        return response;
    }
}
