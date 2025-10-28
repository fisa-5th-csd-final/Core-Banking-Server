package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.application.util.AccountNumberGenerator;
import com.fisa.bank.account.persistence.entity.AccountEntity;
import com.fisa.bank.account.persistence.entity.AccountTransactionEntity;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.account.persistence.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

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

    // 출금 서비스 로직
    @Transactional
    public AccountTransactionResponse withdraw(Long accountId, AccountWithdrawRequest request) {
        AccountId id = AccountId.of(accountId);
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> AccountNotFoundException.EXCEPTION);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw InsufficientBalanceException.EXCEPTION;
        }

        BigDecimal before = account.getBalance();
        BigDecimal after = before.subtract(request.amount());
        account.updateBalance(after);

        AccountTransactionEntity trx = AccountTransactionEntity.builder()
                .account(account)
                .type(TransactionType.WITHDRAW)
                .amount(request.amount())
                .balanceBefore(before)
                .balanceAfter(after)
                .isIncome(false)
                .date(LocalDateTime.now())
                .build();

        AccountTransactionEntity saved = accountTransactionRepository.save(trx);

        return AccountTransactionResponse.of(saved);
    }


}
