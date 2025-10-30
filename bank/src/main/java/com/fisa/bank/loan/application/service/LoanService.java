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

    public void applyForLoan(Long loanProductId) {
        // 해당 id를 가지고 대출 원장성 테이블에 저장
            // 필요한 데이터
                // - body로 받을 데이터: 사용자 id, 원금, 남은 상환원금, 상환 방법, 금리 유형

                // - 최종 금리 = 기본 금리 + 가산 금리 - 우대 금리
                    // ->
                // - 상태 : 정상(초기 디폴트)

                // - 중도 상환 수수료 - 금리 유형, 대출 유형 참고


        // 해당 id를 가지고 대출 이력성 테이블에 '대출' 거래 타입으로 저장
    }
}
