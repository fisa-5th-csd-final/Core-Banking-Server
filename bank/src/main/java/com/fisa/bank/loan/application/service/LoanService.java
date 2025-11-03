package com.fisa.bank.loan.application.service;

import com.fisa.bank.common.presentation.util.SpringRequesterInfo;
import com.fisa.bank.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.interest.application.service.InterestService;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanApplyforResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductResponse;
import com.fisa.bank.loan.application.dto.response.PagedResponse;
import com.fisa.bank.loan.application.exception.DuplicateLoanException;
import com.fisa.bank.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.loan.application.exception.PreferInterestNotFoundException;
import com.fisa.bank.loan.application.model.EarlyRepayInterestRate;
import com.fisa.bank.loan.persistence.entity.LoanLedger;
import com.fisa.bank.loan.persistence.entity.LoanProduct;
import com.fisa.bank.loan.persistence.entity.PreferInterest;
import com.fisa.bank.loan.persistence.entity.PreferInterestCompositeKey;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.InterestType;
import com.fisa.bank.loan.persistence.enums.LoanType;
import com.fisa.bank.loan.persistence.enums.RepaymentStatus;
import com.fisa.bank.loan.persistence.repository.LoanLedgerRepository;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import com.fisa.bank.loan.persistence.repository.PreferInterestRepository;
import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final InterestService interestService;
    private final PreferInterestRepository preferInterestRepository;
    private final UserRepository userRepository;
    private final SpringRequesterInfo springRequesterInfo;
    private final LoanLedgerRepository loanLedgerRepository;

    @Transactional
    public LoanProductCreateResponse createLoanProduct(LoanProductCreateRequest requestDTO){

        LoanProduct loanProduct = loanRepository.save(LoanProduct.builder()
                .name(requestDTO.getName())
                .type(requestDTO.getType())
                .build());

        InterestRateResponse interestRateResponse = interestService.createInterestRate(loanProduct, requestDTO.getAddInterest(), requestDTO.getLimitPreferInterest());

        LoanProductCreateResponse response = LoanProductCreateResponse.builder()
                .name(loanProduct.getName())
                .type(loanProduct.getType())
                .loanProductId(loanProduct.getLoanProductId().getValue())
                .addInterest(interestRateResponse.getAddInterest())
                .limitPreferInterest(interestRateResponse.getLimitPreferInterest())
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

        Page<LoanProductResponse<LoanProduct>> response = productPage.map(
                (loanProduct)->{
                    InterestRate interestRate = loanProduct.getInterestRateList().get(0);
                    InterestRateResponse interestRateResponse = InterestRateResponse.from(interestRate);
                    return LoanProductResponse.from(loanProduct, interestRateResponse);
                }
        );

        return new PagedResponse<>(response);
    }

    @Transactional
    public LoanProductResponse<LoanProduct> findProductById(Long loanProductId) {

        LoanProduct loanProduct = loanRepository.findById(LoanProductId.of(loanProductId))
                .orElseThrow(()-> new LoanProductNotFoundException(loanProductId));

        InterestRateResponse interestRateResponse = InterestRateResponse.from(loanProduct.getInterestRateList().get(0));

        LoanProductResponse<LoanProduct> response = LoanProductResponse.from(loanProduct, interestRateResponse);

        return response;
    }

    @Transactional
    public LoanApplyforResponse applyForLoan(LoanApplyForRequest request, Long loanProductId){

        // 유저 정보 추출
        UserId userId = springRequesterInfo.getUserId();
        User user = userRepository.getReferenceById(userId);
        if(loanLedgerRepository.existsByUser_UserIdAndLoanProduct_LoanProductId(userId, LoanProductId.of(loanProductId))){
            throw new DuplicateLoanException(userId, LoanProductId.of(loanProductId));
        }


        // 대출 원장성 테이블에 저장 LoanLedger
        // 미리 세팅해둘 데이터
            // 남은 상환액(원금) - 초기값은 원금과 동일
        BigDecimal remainPrincipal = request.getPrincipal();
            // 대출 상태 - 초기값은 정상
        RepaymentStatus repaymentStatus = RepaymentStatus.NORMAL;

        // 요청해서 세팅해야 하는 데이터
            // 대출 유형 - 대출 상품에서 조회
        LoanProduct loanProduct = loanRepository.findById(LoanProductId.of(loanProductId))
                .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));
        LoanType loanType = loanProduct.getType();
        InterestType interestType = request.getInterestType();

        // 상환 기간 유효한지 체크
        int term = request.getTerm();
        if(loanType == LoanType.CREDIT && (term < 1 || term >10)){
            throw new IllegalArgumentException("CREDIT 대출의 상환 기간은 1~10년이어야 합니다.");
        }
        else if (loanType == LoanType.MORTGAGE && (term < 1 || term > 50)) {
            throw new IllegalArgumentException("MORTGAGE 대출의 상환 기간은 1~50년이어야 합니다.");
        }

        // 마지막 상환, 바로 다음 상환일 계산
        LocalDateTime startDate = LocalDateTime.now(); // 가입 시점 기준

        // 마지막 상환일 = 시작일 + term 년
        LocalDateTime loanEndDate = startDate.plusYears(term);

        // 첫 번째 상환일 = 시작일 + 1년 (상환 주기 1년 가정)
        LocalDateTime nextRepaymentDate = startDate.plusYears(1);

        InterestRate interestRate = loanProduct.getInterestRateList().get(0);

        // 기본 금리 - 금리 테이블에서 조회
        BigDecimal baseInterest = interestRate.getBaseInterest();
        // 가산 금리 - 금리 테이블에서 조회해야 함.
        BigDecimal addInterest = interestRate.getAddInterest();
        // 최대 우대 금리 - 고객 개인의 우대 금리와 비교해서 더 작은 값으로.
        BigDecimal limitPreferInterest = interestRate.getLimitPreferInterest();

        // 우대 금리 - 유저의 신용등급과 고객등급으로
        CreditRating creditLevel = user.getCreditLevel(); // 신용 등급
        CustomerLevel customerLevel = user.getCustomerLevel(); // 고객 등급

        // 신용, 고객 등급으로 만든 복합키
        PreferInterestCompositeKey key = PreferInterestCompositeKey.builder()
                .creditRating(creditLevel)
                .customerLevel(customerLevel)
                .build();

        // 복합키에 해당하는 우대 금리 조회
        PreferInterest preferInterest = preferInterestRepository.findById(key)
                .orElseThrow(()->new PreferInterestNotFoundException(creditLevel, customerLevel));

            // 중도 상환 수수료 - 금리 유형, 대출 유형으로 조회
        BigDecimal earlyRepayInterestRate = EarlyRepayInterestRate.getEarlyRepayInterestRate(loanType, interestType);
            // 최종 금리 = 기본 금리 + 가산 금리 - 우대 금리
            // 우대 금리 vs 상한 우대 금리
        BigDecimal finalPreferInterest = limitPreferInterest.compareTo(preferInterest.getPreferInterest()) < 0 ? limitPreferInterest : preferInterest.getPreferInterest();

        BigDecimal completedInterest = baseInterest.add(addInterest).subtract(finalPreferInterest);

        // 대출 원장 테이블 데이터 만들기
        LoanLedger loanLedger = LoanLedger.builder()
                .loanProduct(loanProduct)
                .user(user)
                .completedInterest(completedInterest)
                .principal(request.getPrincipal())
                .remainPrincipal(remainPrincipal)
                .repaymentType(request.getRepaymentType())
                .repaymentStatus(repaymentStatus)
                .nextRepaymentDate(nextRepaymentDate)
                .loanEndDate(loanEndDate)
                .overdueCount(0)
                .interestType(interestType)
                .earlyRepayInterestRate(earlyRepayInterestRate)
                .build();
        LoanLedger savedLoanLedger = loanLedgerRepository.save(loanLedger);

        LoanApplyforResponse loanApplyForResponse = LoanApplyforResponse.builder()
//                .loanProductId(savedLoanLedger.getLoanProduct().getLoanProductId().getValue())
                .name(savedLoanLedger.getLoanProduct().getName())
//                .completedInterest(savedLoanLedger.getCompletedInterest())
//                .principal(savedLoanLedger.getPrincipal())
//                .remainPrincipal(savedLoanLedger.getRemainPrincipal())
//                .nextRepaymentDate(savedLoanLedger.getNextRepaymentDate())
//                .nextRepaymentDate(savedLoanLedger.getNextRepaymentDate())
//                .loanEndDate(savedLoanLedger.getLoanEndDate())
//                .repaymentStatus(savedLoanLedger.getRepaymentStatus())
//                .earlyRepayInterestRate(savedLoanLedger.getEarlyRepayInterestRate())
                .build();
        // TODO: 대출 거래 테이블에 저장 LoanTransaction
        return loanApplyForResponse;
    }

}
