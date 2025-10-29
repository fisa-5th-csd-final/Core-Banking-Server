package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.util.AccountNumberGenerator;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private static final String DEFAULT_BANK_CODE = "020";

    // 계좌 생성 서비스
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        User user = userRepository.findById(UserId.of(request.getUserId()))
                .orElseThrow(UserNotFoundException::new);

        String accountNumber = AccountNumberGenerator.generate();

        Account account = Account.create(
                accountNumber,
                user,
                DEFAULT_BANK_CODE
        );

        Account saved = accountRepository.save(account);

        return AccountResponse.of(saved, "계좌가 성공적으로 생성되었습니다.");
    }


}
