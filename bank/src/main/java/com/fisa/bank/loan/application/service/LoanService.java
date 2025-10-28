package com.fisa.bank.loan.application.service;

import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequestDTO;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponseDTO;
import com.fisa.bank.loan.application.exception.LoanProductException;
import com.fisa.bank.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;

    @Transactional
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

    @Transactional
    public void deleteLoanProduct(Long loanProductId) {
        // 있는지 확인 후
        if (!loanRepository.existsById(loanProductId)) {
//            throw new LoanProductNotFoundException(loanProductId);
            throw new IllegalArgumentException("요청하신 대출 상품을 찾을 수 없습니다." + " (ID: " + loanProductId + ")");
        }
        loanRepository.deleteById(loanProductId);
    }
}
