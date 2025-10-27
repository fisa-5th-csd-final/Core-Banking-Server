package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.util.AccountNumberGenerator;
import com.fisa.bank.account.persistence.entity.AccountEntity;
import com.fisa.bank.common.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private static final String DEFAULT_BANK_CODE = "020";

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        String accountNumber = AccountNumberGenerator.generate();

        AccountEntity account = AccountEntity.create(
                accountNumber, // 계좌번호
                request.getUserId(), // 사용자 Id
                DEFAULT_BANK_CODE // 은행 코드
        );

        AccountEntity saved = accountRepository.save(account);

        return AccountResponse.of(saved, "계좌가 성공적으로 생성되었습니다.");
    }
}
