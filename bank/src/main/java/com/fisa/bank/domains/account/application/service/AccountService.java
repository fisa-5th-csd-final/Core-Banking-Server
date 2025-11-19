package com.fisa.bank.domains.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountListResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountResponse;
import com.fisa.bank.domains.account.application.exception.AccountNotDeletableException;
import com.fisa.bank.domains.account.application.service.reader.AccountReader;
import com.fisa.bank.domains.account.application.util.AccountNumberGenerator;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.common.aop.annotation.DomainType;
import com.fisa.bank.domains.common.aop.annotation.VerifyOwner;
import com.fisa.bank.domains.loan.persistence.enums.RepaymentStatus;
import com.fisa.bank.domains.user.persistence.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountCreator accountCreator;
  private final AccountRepository accountRepository;
  private final AccountReader accountReader;

  // 계좌 생성 서비스
  @Transactional
  public AccountResponse createAccount(Long userId) {
    User user = accountReader.getUserById(userId);
    Account account = accountCreator.createNonIncomeAccount(user);

    Account saved = accountRepository.save(account);

    return AccountResponse.from(saved, "계좌가 성공적으로 생성되었습니다.");
  }

  // 계좌 상세 조회
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  @Transactional(readOnly = true)
  public AccountDetailResponse getAccountDetail(String accountNumber) {
    Account account = accountReader.getAccountByAccountNumber(accountNumber);
    return AccountDetailResponse.from(account);
  }

  // 유저별 계좌 조회
  @Transactional(readOnly = true)
  public List<AccountListResponse> getAccountsByUserId(Long userId) {
    User user = accountReader.getUserById(userId);
    return accountRepository.findAllByUser(user).stream().map(AccountListResponse::from).toList();
  }

  // 계좌 삭제
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  @Transactional
  public void deleteAccount(String accountNumber) {
    Account account = accountReader.getAccountByAccountNumber(accountNumber);

    // 급여 계좌인 경우 삭제 불가
      if(account.isForIncome()) throw new AccountNotDeletableException("급여 계좌는 삭제할 수 없습니다.");

    // 잔액이 있는 경우 예외 처리
    if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
      throw new AccountNotDeletableException();
    }

    // 연결된 대출 상품이 있을 경우 삭제 불가
    if (account.getLoanLedger() != null) {
      if (account.getLoanLedger().getRepaymentStatus() == RepaymentStatus.NORMAL
          || account.getLoanLedger().getRepaymentStatus() == RepaymentStatus.OVERDUE) {
        throw new AccountNotDeletableException("연결된 대출 상품이 있는 계좌는 삭제할 수 없습니다.");
      }
    }

    accountRepository.delete(account);
  }
}
