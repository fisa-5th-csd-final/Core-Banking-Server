package com.fisa.bank.loan.application.service;

import com.fisa.bank.common.presentation.response.code.BusinessErrorCode;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductResponse;
import com.fisa.bank.loan.application.dto.response.PagedResponse;
import com.fisa.bank.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductIdJavaType;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;

    @Transactional
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

    @Transactional
    public void deleteLoanProduct(Long loanProductId) {
        // 있는지 확인 후
        if (!loanRepository.existsById(LoanProductId.of(loanProductId))) {
            throw new LoanProductNotFoundException(loanProductId);
        }
        loanRepository.deleteById(LoanProductId.of(loanProductId));
    }

    @Transactional
    public PagedResponse<LoanProductResponse<LoanProduct>> findAllProducts(Pageable pageable) {
        Page<LoanProduct> productPage = loanRepository.findAll(pageable);

        Page<LoanProductResponse<LoanProduct>> response = productPage.map(LoanProductResponse::from);


        return new PagedResponse<>(response);
    }
}
