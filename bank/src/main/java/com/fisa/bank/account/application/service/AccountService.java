package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.account.application.dto.response.AccountListResponse;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.exception.AccessDeniedException;
import com.fisa.bank.account.application.exception.AccountNotDeletableException;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.application.util.AccountNumberGenerator;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountReader accountReader;

    private static final String DEFAULT_BANK_CODE = "020";

    // 계좌 생성 서비스
    @Transactional
    public AccountResponse createAccount() {
        User user = accountReader.getUserById();
        String accountNumber = AccountNumberGenerator.generate();
        Account account = Account.create(
                accountNumber,
                user,
                DEFAULT_BANK_CODE
        );

        Account saved = accountRepository.save(account);

        return AccountResponse.of(saved, "계좌가 성공적으로 생성되었습니다.");
    }

    // 계좌 상세 조회
    @Transactional(readOnly = true)
    public AccountDetailResponse getAccountDetail(String accountNumber) {
      Account account = accountReader.getOwnedAccount(accountNumber);
      return AccountDetailResponse.of(account);
    }

    // 유저별 계좌 조회
    @Transactional(readOnly = true)
    public List<AccountListResponse> getAccountsByUserId() {
        User user = accountReader.getUserById();

        return accountRepository.findAllByUser(user).stream()
                .map(AccountListResponse::of)
                .toList();
    }

    // 계좌 삭제
    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountReader.getOwnedAccount(accountNumber);

        // 잔액이 있는 경우 예외 처리
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountNotDeletableException();
        }

        accountRepository.delete(account);
    }

}
