package com.fisa.bank.account.application.service;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.request.CardPaymentRequest;
import com.fisa.bank.account.application.dto.request.TransferRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionListResponse;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.dto.response.CardPaymentResponse;
import com.fisa.bank.account.application.dto.response.TransferResponse;
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.application.exception.InvalidTransferTargetException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.account.persistence.repository.AccountTransactionRepository;
import com.fisa.bank.account.persistence.repository.CardTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final AccountReader accountReader;

    @Value("${bank.code}")
    private String ourBankCode;

    // 거래 시 거래 전, 거래 후 금액, 잔액 부족 등의 공통의 로직을 작성
    private AccountTransaction recordTransaction(
            Account account,
            BigDecimal amount,
            TransactionType type,
            boolean isIncome,
            String destinationAccount) {
        BigDecimal before = account.getBalance();
        BigDecimal after = isIncome ? before.add(amount) : before.subtract(amount);

        // 출금 시 잔액 부족 검증
        if (!isIncome && before.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        account.updateBalance(after);

        AccountTransaction trx =
                AccountTransaction.builder()
                        .account(account)
                        .type(type)
                        .amount(amount)
                        .balanceBefore(before)
                        .balanceAfter(after)
                        .isIncome(isIncome)
                        .date(LocalDateTime.now())
                        .destinationAccount(destinationAccount)
                        .build();

        return accountTransactionRepository.save(trx);
    }

    // 출금
    @Transactional
    public AccountTransactionResponse withdraw(String accountNumber, AccountWithdrawRequest request, Long userId) {
        // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
        Account account = accountReader.getOwnedAccountWithLock(accountNumber, userId);

        AccountTransaction trx =
                recordTransaction(account, request.amount(), TransactionType.ATM_WITHDRAW, false, null);

        return AccountTransactionResponse.from(trx);
    }

    // 입금
    @Transactional
    public AccountTransactionResponse deposit(String accountNumber, AccountDepositRequest request, Long userId) {
        // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
        Account account = accountReader.getOwnedAccountWithLock(accountNumber, userId);

        AccountTransaction trx =
                recordTransaction(account, request.amount(), TransactionType.ATM_DEPOSIT, true, null);

        return AccountTransactionResponse.from(trx);
    }

    // 송금
    @Transactional
    public TransferResponse transfer(TransferRequest request, Long userId) {
        String fromNo = request.fromAccountNumber();
        String toNo = request.toAccountNumber();
        BigDecimal amount = request.amount();

        // 자기 자신으로 송금 방지
        if (ourBankCode.equals(request.toBankCode()) && fromNo.equals(toNo)) {
            throw new InvalidTransferTargetException();
        }

        if (ourBankCode.equals(request.toBankCode())) {
            // 같은 은행: 두 계좌 모두 조회 및 락 획득
            AccountReader.TransferAccountsPair accounts = accountReader.lockTransferAccounts(fromNo, toNo, userId);

            // 거래 처리
            recordTransaction(accounts.from(), amount, TransactionType.TRANSFER_SEND, false, accounts.to().getAccountNumber());
            recordTransaction(accounts.to(), amount, TransactionType.TRANSFER_RECEIVE, true, accounts.from().getAccountNumber());

            return TransferResponse.of(accounts.from(), accounts.to(), amount);

        } else {
            // 타행: 보내는 쪽만 락
            Account from = accountReader.getOwnedAccountWithLock(fromNo, userId);
            recordTransaction(from, amount, TransactionType.EXTERNAL_TRANSFER_SEND, false, toNo);
            return TransferResponse.ofExternal(from, toNo, amount);
        }
    }


    // 카드 결제
    @Transactional
    public CardPaymentResponse payByCard(String accountNumber, CardPaymentRequest request, Long userId) {
        // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
        Account account = accountReader.getOwnedAccountWithLock(accountNumber, userId);

        // 계좌에도 로그 남기기위해 반영
        recordTransaction(
                account,
                request.amount(),
                TransactionType.CARD_PAYMENT,
                false,
                request.storeName() // destinationAccount 대신 storeName 기록
        );

        // 💾 카드 결제 내역 추가 저장
        CardTransaction cardTrx =
                CardTransaction.builder()
                        .account(account)
                        .amount(request.amount())
                        .storeName(request.storeName())
                        .category(request.category())
                        .build();

        CardTransaction saved = cardTransactionRepository.save(cardTrx);

        return CardPaymentResponse.from(saved);
    }

    public AccountTransactionListResponse getTransactions(
            String accountNumber, LocalDate startDate, LocalDate endDate, Long userId) {
        // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
        Account account = accountReader.getOwnedAccount(accountNumber, userId);

        // 거래내역 조회
        List<AccountTransactionResponse> transactions =
                accountTransactionRepository
                        .findByAccountAndDateGreaterThanEqualAndDateBefore(
                                account, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
                        .stream()
                        .map(AccountTransactionResponse::from)
                        .toList();

        // 응답 DTO 생성
        return new AccountTransactionListResponse(account.getAccountNumber(), transactions);
    }
}
