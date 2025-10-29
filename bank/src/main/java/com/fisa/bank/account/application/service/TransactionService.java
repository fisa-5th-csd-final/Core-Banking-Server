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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    // 거래 시 거래 전, 거래 후 금액, 잔액 부족 등의 공통의 로직을 작성
    private AccountTransaction processTransaction(
            Account account,
            BigDecimal amount,
            TransactionType type,
            boolean isIncome
    ) {
        BigDecimal before = account.getBalance();
        BigDecimal after = isIncome ? before.add(amount) : before.subtract(amount);

        // 출금 시 잔액 부족 검증
        if (!isIncome && before.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        account.updateBalance(after);

        AccountTransaction trx = AccountTransaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .isIncome(isIncome)
                .date(LocalDateTime.now())
                .build();

        return accountTransactionRepository.save(trx);
    }

    // 출금
    @Transactional
    public AccountTransactionResponse withdraw(Long accountId, AccountWithdrawRequest request) {
        Account account = accountRepository.findById(AccountId.of(accountId))
                .orElseThrow(AccountNotFoundException::new);

        AccountTransaction trx = processTransaction(
                account,
                request.amount(),
                TransactionType.ATM_WITHDRAW,
                false
        );

        return AccountTransactionResponse.of(trx);
    }

    // 입금
    @Transactional
    public AccountTransactionResponse deposit(Long accountId, AccountDepositRequest request) {
        Account account = accountRepository.findById(AccountId.of(accountId))
                .orElseThrow(AccountNotFoundException::new);

        AccountTransaction trx = processTransaction(
                account,
                request.amount(),
                TransactionType.ATM_DEPOSIT,
                true
        );

        return AccountTransactionResponse.of(trx);
    }
}
