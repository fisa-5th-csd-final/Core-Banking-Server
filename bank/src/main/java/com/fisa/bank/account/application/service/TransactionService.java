package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
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
public class TransactionService {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    // 출금 서비스
    @Transactional
    public AccountTransactionResponse withdraw(Long accountId, AccountWithdrawRequest request) {
        AccountId id = AccountId.of(accountId);
        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException();
        }

        BigDecimal before = account.getBalance();
        BigDecimal after = before.subtract(request.amount());
        account.updateBalance(after);

        AccountTransaction trx = AccountTransaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAW)
                .amount(request.amount())
                .balanceBefore(before)
                .balanceAfter(after)
                .isIncome(false)
                .date(LocalDateTime.now())
                .build();

        AccountTransaction saved = accountTransactionRepository.save(trx);

        return AccountTransactionResponse.of(saved);
    }

    // 예금 서비스
    @Transactional
    public AccountTransactionResponse deposit(Long accountId, AccountDepositRequest request) {
        AccountId id = AccountId.of(accountId);

        Account account = accountRepository.findById(id)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(request.amount());

        account.updateBalance(after);

        AccountTransaction trx = AccountTransaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .balanceBefore(before)
                .balanceAfter(after)
                .isIncome(true)
                .date(LocalDateTime.now())
                .build();

        AccountTransaction saved = accountTransactionRepository.save(trx);
        return AccountTransactionResponse.of(saved);
    }

}
