package com.fisa.bank.loan.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.common.aop.annotation.DomainType;
import com.fisa.bank.common.aop.annotation.VerifyOwner;
import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.interest.application.service.InterestService;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.loan.application.dto.request.LoanMonthlyRepayRequest;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.LoanApplyforResponse;
import com.fisa.bank.loan.application.dto.response.LoanLedgerDetailResponse;
import com.fisa.bank.loan.application.dto.response.LoanLedgerResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductCreateResponse;
import com.fisa.bank.loan.application.dto.response.LoanProductResponse;
import com.fisa.bank.loan.application.dto.response.LoanTransactionResponse;
import com.fisa.bank.loan.application.dto.response.PagedResponse;
import com.fisa.bank.loan.application.event.LoanCancelledEvent;
import com.fisa.bank.loan.application.event.LoanRepaidEvent;
import com.fisa.bank.loan.application.exception.*;
import com.fisa.bank.loan.application.model.EarlyRepayment;
import com.fisa.bank.loan.application.model.MonthlyRepayment;
import com.fisa.bank.loan.application.model.UpdateLoanLedgerParam;
import com.fisa.bank.loan.application.service.calculator.CalculatorService;
import com.fisa.bank.loan.application.service.reader.LoanReader;
import com.fisa.bank.loan.application.util.EarlyRepayInterestRate;
import com.fisa.bank.loan.application.util.LoanTransactionFactory;
import com.fisa.bank.loan.persistence.entity.*;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.*;
import com.fisa.bank.loan.persistence.repository.LoanLedgerRepository;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import com.fisa.bank.loan.persistence.repository.LoanTransactionRepository;
import com.fisa.bank.loan.persistence.repository.PreferInterestRepository;
import com.fisa.bank.user.application.service.reader.UserReader;
import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;

@Service
@RequiredArgsConstructor
public class LoanService {
  private final LoanRepository loanRepository;
  private final InterestService interestService;
  private final PreferInterestRepository preferInterestRepository;
  private final UserReader userReader;
  private final LoanLedgerRepository loanLedgerRepository;
  private final LoanTransactionRepository loanTransactionRepository;
  private final AccountReader accountReader;
  private final CalculatorService calculatorService;
  private final LoanReader loanReader;
  private final RequesterInfo requesterInfo;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public LoanProductCreateResponse createLoanProduct(LoanProductCreateRequest requestDTO) {

    LoanProduct loanProduct =
        loanRepository.save(
            LoanProduct.builder().name(requestDTO.getName()).type(requestDTO.getType()).build());

    InterestRateResponse interestRateResponse =
        interestService.createInterestRate(
            loanProduct, requestDTO.getAddInterest(), requestDTO.getLimitPreferInterest());

    LoanProductCreateResponse response =
        LoanProductCreateResponse.builder()
            .name(loanProduct.getName())
            .type(loanProduct.getType())
            .loanProductId(loanProduct.getLoanProductId().getValue())
            .addInterest(interestRateResponse.getAddInterest())
            .limitPreferInterest(interestRateResponse.getLimitPreferInterest())
            .build();

    return response;
  }

  // TODO: 추후 soft-delete로 바꿀 예정
  @Transactional
  public void deleteLoanProduct(Long loanProductId) {
    // 있는지 확인 후
    if (!loanRepository.existsById(LoanProductId.of(loanProductId))) {
      throw new LoanProductNotFoundException(loanProductId);
    }
    loanRepository.deleteById(LoanProductId.of(loanProductId));
  }

  @Transactional
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public LoanApplyforResponse applyForLoan(LoanApplyForRequest request, Long loanProductId) {
    UserId userId = requesterInfo.getUserId();
    // 유저 정보 추출
    User user = userReader.getUserById(userId.getValue());
    Account account = accountReader.getAccountByAccountNumber(request.getAccountNumber());
    if (loanReader.existsByUserIdAndLoanProductId(userId.getValue(), loanProductId)) {
      throw new DuplicateLoanException(userId, LoanProductId.of(loanProductId));
    }

    // 대출 원장성 테이블에 저장 LoanLedger
    // 남은 상환액(원금) - 초기값은 원금과 동일
    BigDecimal remainPrincipal = request.getPrincipal();

    // 대출 유형 - 대출 상품에서 조회
    LoanProduct loanProduct = loanReader.findProductById(loanProductId);
    LoanType loanType = loanProduct.getType();
    InterestType interestType = request.getInterestType();

    // 상환 기간 유효한지 체크
    int term = request.getTerm();
    if (loanType == LoanType.CREDIT && (term < 1 || term > 10)) {
      throw new IllegalArgumentException("CREDIT 대출의 상환 기간은 1~10년이어야 합니다.");
    } else if (loanType == LoanType.MORTGAGE && (term < 1 || term > 50)) {
      throw new IllegalArgumentException("MORTGAGE 대출의 상환 기간은 1~50년이어야 합니다.");
    }

    // 마지막 상환, 바로 다음 상환일 계산
    LocalDateTime startDate = LocalDateTime.now(); // 가입 시점 기준

    // 마지막 상환일 = 시작일 + term 년
    LocalDateTime loanEndDate = startDate.plusYears(term);

    // 첫 번째 상환일 = 시작일 + 1개월 (상환 주기 1년 가정)
    LocalDateTime nextRepaymentDate = startDate.plusMonths(1);

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
    PreferInterestCompositeKey key =
        PreferInterestCompositeKey.builder()
            .creditRating(creditLevel)
            .customerLevel(customerLevel)
            .build();

    // 복합키에 해당하는 우대 금리 조회
    PreferInterest preferInterest =
        preferInterestRepository
            .findById(key)
            .orElseThrow(() -> new PreferInterestNotFoundException(creditLevel, customerLevel));

    // 중도 상환 수수료 - 금리 유형, 대출 유형으로 조회
    BigDecimal earlyRepayInterestRate =
        EarlyRepayInterestRate.getEarlyRepayInterestRate(loanType, interestType);

    // 우대 금리 vs 상한 우대 금리 중 낮은 금리 선택
    BigDecimal finalPreferInterest =
        limitPreferInterest.compareTo(preferInterest.getPreferInterest()) < 0
            ? limitPreferInterest
            : preferInterest.getPreferInterest();

    // 최종 금리 = 기본 금리 + 가산 금리 - 최종 우대 금리
    BigDecimal completedInterest = baseInterest.add(addInterest).subtract(finalPreferInterest);

    // 대출 원장 테이블 데이터 만들기
    LoanLedger loanLedger =
        LoanLedger.createLoanLedger(
            loanProduct,
            user,
            completedInterest,
            request.getPrincipal(),
            remainPrincipal,
            request.getRepaymentType(),
            nextRepaymentDate,
            loanEndDate,
            interestType,
            earlyRepayInterestRate,
            request.getTerm(),
            account);

    // 대출 이력성 테이블에 저장 LoanTransaction
    LoanTransaction loanTransaction =
        LoanTransactionFactory.createLoan(loanLedger, remainPrincipal, startDate);
    loanLedger.addLoanTransactionList(loanTransaction);

    LoanLedger savedLoanLedger = loanLedgerRepository.save(loanLedger);
    loanTransactionRepository.save(loanTransaction);

    LoanApplyforResponse loanApplyForResponse =
        LoanApplyforResponse.builder().name(savedLoanLedger.getLoanProduct().getName()).build();

    return loanApplyForResponse;
  }

  @Transactional
  @VerifyOwner(domain = DomainType.LOAN, idParam = "loanLedgerId")
  public LoanTransactionResponse repayMonthlyLoan(
      Long loanLedgerId, LoanMonthlyRepayRequest request) {

    LoanLedger loanLedger = loanReader.findLoanLedgerById(loanLedgerId);

    MonthlyRepayment monthlyRepayment = calculatorService.calculate(loanLedger);

    // 납입 금액이 상환금보다 작은지 확인
    if (request.getAmount().compareTo(monthlyRepayment.getMonthlyPayment()) < 0) {
      throw new InsufficientRepaymentException(
          request.getAmount(), monthlyRepayment.getMonthlyPayment());
    }

    Account account = loanLedger.getAccount();

    if (account.getBalance().compareTo(monthlyRepayment.getMonthlyPayment()) < 0) {
      throw new InsufficientBalanceException();
    }

    // 상환 가능하다면, 원장 테이블 업데이트 후 이력성 테이블에 데이터 저장
    // 남은 원금, 다음 상환일, 마지막 상환 날짜 업데이트
    LocalDateTime lastRepaymentDate = loanLedger.getNextRepaymentDate();
    LocalDateTime nextRepaymentDate = lastRepaymentDate.plusMonths(1);

    loanLedger.updateLoanLedger(
        UpdateLoanLedgerParam.builder()
            .remainPrincipal(monthlyRepayment.getRemainPrincipal())
            .lastRepaymentDate(lastRepaymentDate)
            .nextRepaymentDate(nextRepaymentDate)
            .status(loanLedger.getRepaymentStatus())
            .build());

    // 이력성 테이블에도 저장
    LoanTransaction loanTransaction =
        LoanTransactionFactory.createRepay(
            loanLedger,
            monthlyRepayment.getMonthlyPayment(),
            monthlyRepayment,
            LocalDateTime.now());

    loanTransactionRepository.save(loanTransaction);

    eventPublisher.publishEvent(
        new LoanRepaidEvent(
            account, monthlyRepayment.getMonthlyPayment(), loanLedger.getLoanProduct().getName()));

    return LoanTransactionResponse.from(loanTransaction);
  }

  @Transactional
  @VerifyOwner(domain = DomainType.LOAN, idParam = "loanLedgerId")
  public void cancelLoan(Long loanLedgerId) {
    LocalDateTime today = LocalDateTime.now();

    LoanLedger loanLedger = loanReader.findLoanLedgerById(loanLedgerId);

    Account account = loanLedger.getAccount();

    // 수수료율
    BigDecimal earlyPaidRate = loanLedger.getEarlyRepayInterestRate();

    // 중도 상환 금액 계산
    EarlyRepayment earlyRepayment = EarlyRepayment.create(loanLedger, today, earlyPaidRate);

    if (account.getBalance().compareTo(earlyRepayment.getMustPaidAmount()) < 0) {
      throw new InsufficientBalanceException();
    }

    loanLedger.updateLoanLedger(
        UpdateLoanLedgerParam.builder()
            .remainPrincipal(BigDecimal.ZERO)
            .lastRepaymentDate(today)
            .nextRepaymentDate(null)
            .status(RepaymentStatus.TERMINATED)
            .build());

    LoanTransaction loanTransaction =
        LoanTransactionFactory.createEarlyRepay(loanLedger, earlyRepayment, today);

    loanLedger.addLoanTransactionList(loanTransaction);

    loanTransactionRepository.save(loanTransaction);

    eventPublisher.publishEvent(
        new LoanCancelledEvent(
            account, earlyRepayment.getMustPaidAmount(), loanLedger.getLoanProduct().getName()));
  }

  @Transactional(readOnly = true)
  public PagedResponse<LoanProductResponse<LoanProduct>> getAllProducts(Pageable pageable) {
    Page<LoanProduct> productPage = loanReader.findAllProducts(pageable);

    Page<LoanProductResponse<LoanProduct>> responsePage =
        productPage.map(
            loanProduct -> {
              InterestRate interestRate = loanProduct.getInterestRateList().get(0);
              InterestRateResponse interestRateResponse = InterestRateResponse.from(interestRate);
              return LoanProductResponse.from(loanProduct, interestRateResponse);
            });

    return new PagedResponse<>(responsePage);
  }

  @Transactional(readOnly = true)
  public LoanProductResponse<LoanProduct> getProductById(Long loanProductId) {
    LoanProduct loanProduct = loanReader.findProductById(loanProductId);

    InterestRateResponse interestRateResponse =
        InterestRateResponse.from(loanProduct.getInterestRateList().get(0));

    return LoanProductResponse.from(loanProduct, interestRateResponse);
  }

  @Transactional(readOnly = true)
  @VerifyOwner(domain = DomainType.LOAN, idParam = "loanLedgerId")
  public LoanLedgerDetailResponse getLoanLedgerDetail(Long loanLedgerId) {
    LoanLedger loanLedger = loanReader.findLoanLedgerById(loanLedgerId);

    MonthlyRepayment monthlyRepayment = calculatorService.calculate(loanLedger);
    return LoanLedgerDetailResponse.from(loanLedger, monthlyRepayment);
  }

  @Transactional(readOnly = true)
  public List<LoanLedgerResponse> getMyLoanLedgers() {
    Long userId = requesterInfo.getUserId().getValue();
    List<LoanLedger> loanLedgers = loanReader.findAllByUserId(userId);
    return loanLedgers.stream().map(LoanLedgerResponse::from).toList();
  }
}
