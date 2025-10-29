package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountCreateRequest;
import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
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
import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserRepository;
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
    private final UserRepository userRepository;

    private static final String DEFAULT_BANK_CODE = "020";

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        User user = userRepository.findById(UserId.of(request.getUserId()))
                .orElseThrow(UserNotFoundException::new);

        String accountNumber = AccountNumberGenerator.generate();

        AccountEntity account = AccountEntity.create(
                accountNumber,
                user,
                DEFAULT_BANK_CODE
        );

        AccountEntity saved = accountRepository.save(account);

        return AccountResponse.of(saved, "계좌가 성공적으로 생성되었습니다.");
    }

    // 출금 서비스 로직
    @Transactional
    public AccountTransactionResponse withdraw(Long accountId, AccountWithdrawRequest request) {
        AccountId id = AccountId.of(accountId);
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException();
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

    @Transactional
    public AccountTransactionResponse deposit(Long accountId, AccountDepositRequest request) {
        AccountId id = AccountId.of(accountId);

        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(request.amount());

        account.updateBalance(after);

        AccountTransactionEntity trx = AccountTransactionEntity.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .balanceBefore(before)
                .balanceAfter(after)
                .isIncome(true)
                .date(LocalDateTime.now())
                .build();

        accountTransactionRepository.save(trx);

        return AccountTransactionResponse.of(trx);
    }



}
