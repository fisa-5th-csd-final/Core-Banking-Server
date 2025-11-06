package com.fisa.bank.loan.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.interest.application.dto.response.InterestRateResponse;
import com.fisa.bank.interest.application.service.InterestService;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.application.dto.request.LoanApplyForRequest;
import com.fisa.bank.loan.application.dto.request.LoanMonthlyRepayRequest;
import com.fisa.bank.loan.application.dto.request.LoanProductCreateRequest;
import com.fisa.bank.loan.application.dto.response.*;
import com.fisa.bank.loan.application.exception.*;
import com.fisa.bank.loan.application.model.EarlyRepayment;
import com.fisa.bank.loan.application.model.MonthlyRepayment;
import com.fisa.bank.loan.application.model.UpdateLoanLedgerParam;
import com.fisa.bank.loan.application.service.calculator.*;
import com.fisa.bank.loan.application.service.calculator.CalculatorService;
import com.fisa.bank.loan.application.util.EarlyRepayInterestRate;
import com.fisa.bank.loan.application.util.LoanTransactionFactory;
import com.fisa.bank.loan.persistence.entity.*;
import com.fisa.bank.loan.persistence.entity.id.LoanLedgerId;
import com.fisa.bank.loan.persistence.entity.id.LoanProductId;
import com.fisa.bank.loan.persistence.enums.*;
import com.fisa.bank.loan.persistence.repository.LoanLedgerRepository;
import com.fisa.bank.loan.persistence.repository.LoanRepository;
import com.fisa.bank.loan.persistence.repository.LoanTransactionRepository;
import com.fisa.bank.loan.persistence.repository.PreferInterestRepository;
import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoanService {
  private final LoanRepository loanRepository;
  private final InterestService interestService;
  private final PreferInterestRepository preferInterestRepository;
  private final UserRepository userRepository;
  private final LoanLedgerRepository loanLedgerRepository;
  private final LoanTransactionRepository loanTransactionRepository;
  private final RequesterInfo requesterInfo;
  private final AccountRepository accountRepository;
  private final CalculatorService calculatorService;

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

    Page<LoanProductResponse<LoanProduct>> response =
        productPage.map(
            (loanProduct) -> {
              InterestRate interestRate = loanProduct.getInterestRateList().get(0);
              InterestRateResponse interestRateResponse = InterestRateResponse.from(interestRate);
              return LoanProductResponse.from(loanProduct, interestRateResponse);
            });

    return new PagedResponse<>(response);
  }

  @Transactional
  public LoanProductResponse<LoanProduct> findProductById(Long loanProductId) {

    LoanProduct loanProduct =
        loanRepository
            .findById(LoanProductId.of(loanProductId))
            .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));

    InterestRateResponse interestRateResponse =
        InterestRateResponse.from(loanProduct.getInterestRateList().get(0));

    LoanProductResponse<LoanProduct> response =
        LoanProductResponse.from(loanProduct, interestRateResponse);

    return response;
  }

  @Transactional
  public LoanApplyforResponse applyForLoan(LoanApplyForRequest request, Long loanProductId) {

    // 유저 정보 추출
    UserId userId = requesterInfo.getUserId();
    User user = userRepository.getReferenceById(userId);
    Account account =
        accountRepository
            .findByAccountNumber(request.getAccountNumber())
            .orElseThrow(AccountNotFoundException::new);
    if (loanLedgerRepository.existsByUser_UserIdAndLoanProduct_LoanProductId(
        userId, LoanProductId.of(loanProductId))) {
      throw new DuplicateLoanException(userId, LoanProductId.of(loanProductId));
    }

    // 대출 원장성 테이블에 저장 LoanLedger
    // 남은 상환액(원금) - 초기값은 원금과 동일
    BigDecimal remainPrincipal = request.getPrincipal();

    // 대출 유형 - 대출 상품에서 조회
    LoanProduct loanProduct =
        loanRepository
            .findById(LoanProductId.of(loanProductId))
            .orElseThrow(() -> new LoanProductNotFoundException(loanProductId));
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
  public LoanTransactionResponse repayMonthlyLoan(
      Long loanLedgerId, LoanMonthlyRepayRequest request) {

    LoanLedger loanLedger =
        loanLedgerRepository
            .findById(LoanLedgerId.of(loanLedgerId))
            .orElseThrow(() -> new LoanLedgerNotFoundException(loanLedgerId));

    switch (loanLedger.getRepaymentType()) {
      case EQUAL_INSTALLMENT:
        calculatorService.setLoanCalculator(new EqualInstallmentCalculator());
        break;
      case EQUAL_PRINCIPAL:
        calculatorService.setLoanCalculator(new EqualPrincipalCalculator());
        break;
      case BULLET:
        calculatorService.setLoanCalculator(new BulletCalculator());
        break;
      default:
        throw new UnknownCalculatorException();
    }

    MonthlyRepayment monthlyRepayment = calculatorService.calculate(loanLedger);

    // 납입 금액이 상환금보다 작은지 확인
    if (request.getAmount().compareTo(monthlyRepayment.getMonthlyPayment()) < 0) {
      throw new InsufficientRepaymentException(
          request.getAmount(), monthlyRepayment.getMonthlyPayment());
    }

    Account account = loanLedger.getAccount();

    if (account.getBalance().compareTo(monthlyRepayment.getMonthlyPayment()) < 0) {
      throw new InSufficientBalanceAmountException();
    }

    BigDecimal afterBalance = account.getBalance().subtract(monthlyRepayment.getMonthlyPayment());

    account.updateBalance(afterBalance); // 잔액 변경

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

    return LoanTransactionResponse.from(loanTransaction);
  }

  @Transactional
  public void cancelLoan(Long loanLedgerId) {
    LocalDateTime today = LocalDateTime.now();
    UserId userId = requesterInfo.getUserId();

    LoanLedger loanLedger =
        loanLedgerRepository
            .findById(LoanLedgerId.of(loanLedgerId))
            .orElseThrow(() -> new LoanLedgerNotFoundException(loanLedgerId));

    UserId userIdOfLedger = loanLedger.getUser().getUserId();
    Account account = loanLedger.getAccount();

    // 내 대출인지 확인
    if (!userIdOfLedger.equals(userId)) throw new LoanLedgerAccessDeniedException();

    // 수수료율
    BigDecimal earlyPaidRate = loanLedger.getEarlyRepayInterestRate();

    // 중도 상환 금액 계산
    EarlyRepayment earlyRepayment = EarlyRepayment.create(loanLedger, today, earlyPaidRate);

    if (account.getBalance().compareTo(earlyRepayment.getMustPaidAmount()) < 0) {
      throw new InSufficientBalanceAmountException();
    }

    BigDecimal afterBalance = account.getBalance().subtract(earlyRepayment.getMustPaidAmount());

    loanLedger.updateLoanLedger(
        UpdateLoanLedgerParam.builder()
            .remainPrincipal(BigDecimal.ZERO)
            .lastRepaymentDate(today)
            .nextRepaymentDate(null)
            .status(RepaymentStatus.TERMINATED)
            .build());

    account.updateBalance(afterBalance); // 잔액 변경

    LoanTransaction loanTransaction =
        LoanTransactionFactory.createEarlyRepay(loanLedger, earlyRepayment, today);

    loanLedger.addLoanTransactionList(loanTransaction);

    loanTransactionRepository.save(loanTransaction);
  }

  @Transactional(readOnly = true)
  public LoanLedgerDetailResponse getLoanLedgerDetail(Long loanLedgerId) {
    // 대출 이름, 남은 원금, 원금, 월 상환액, 상환 계좌, 대출 유형, 상환 방식 응답
    // TODO: 월 상환액, 상환 계좌 추가해야 됨.
    LoanLedger loanLedger =
        loanLedgerRepository
            .findById(LoanLedgerId.of(loanLedgerId))
            .orElseThrow(() -> new LoanLedgerNotFoundException(loanLedgerId));
    UserId userIdOfLoanLedger = loanLedger.getUser().getUserId();

    UserId userId = requesterInfo.getUserId();
    // 대출한 유저의 id와 로그인한 유저의 id 비교
    if (!userIdOfLoanLedger.equals(userId)) {
      throw new LoanLedgerAccessDeniedException();
    }
    return LoanLedgerDetailResponse.from(loanLedger);
  }

  @Transactional(readOnly = true)
  public List<LoanLedgerResponse> getMyLoanLedger(Long userId) {

    Long userIdLogin = requesterInfo.getUserId().getValue();
    if (!Objects.equals(userIdLogin, userId)) {
      throw new LoanLedgerAccessDeniedException();
    }

    List<LoanLedger> allLoanLedgers = loanLedgerRepository.findAllByUser_UserId(UserId.of(userId));

    return allLoanLedgers.stream().map(LoanLedgerResponse::from).toList();
  }
}
