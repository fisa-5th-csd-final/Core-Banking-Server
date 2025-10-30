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

    @Transactional
    public void applyForLoan(){
        // 받아야 하는 데이터
            // 유저 아이디, 대출 상품 아이디, 원금, 금리 유형
            // 상환 방법(원리금, 원금, 만기)

        // 미리 세팅해둘 데이터
            // 남은 상환액(원금) - 초기값은 원금과 동일
            // 대출 상태 - 초기값은 정상

        // 요청해서 세팅해야 하는 데이터
            // 대출 유형 - 대출 상품에서 조회
            // 우대 금리 - 유저의 신용등급과 고객등급으로 조회
            // 중도 상환 수수료 - 금리 유형, 대출 유형으로 조회
            // 기본 금리 - Loan 도메인에 전역변수로 선언
            // TODO: 가산 금리, 최종 금리
            // 가산 금리 - 대출 상품의 id로 금리 테이블에서 조회해야 함.
            // 최종 금리 = 기본 금리 + 가산 금리 - 우대 금리
    }
}
