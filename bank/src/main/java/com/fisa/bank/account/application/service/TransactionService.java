package com.fisa.bank.account.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.account.application.dto.request.CardPaymentRequest;
import com.fisa.bank.account.application.dto.request.TransferRequest;
import com.fisa.bank.account.application.dto.response.AccountTransactionListResponse;
import com.fisa.bank.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.account.application.dto.response.CardPaymentResponse;
import com.fisa.bank.account.application.dto.response.TransferResponse;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.account.persistence.repository.AccountTransactionRepository;
import com.fisa.bank.account.persistence.repository.CardTransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final AccountTransactionRepository accountTransactionRepository;
  private final CardTransactionRepository cardTransactionRepository;
  private final AccountReader accountReader;

  // 거래 시 거래 전, 거래 후 금액, 잔액 부족 등의 공통의 로직을 작성
  private AccountTransaction processTransaction(
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
  public AccountTransactionResponse withdraw(String accountNumber, AccountWithdrawRequest request) {
    // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
    Account account = accountReader.getOwnedAccount(accountNumber);

    AccountTransaction trx =
        processTransaction(account, request.amount(), TransactionType.ATM_WITHDRAW, false, null);

    return AccountTransactionResponse.of(trx);
  }

  // 입금
  @Transactional
  public AccountTransactionResponse deposit(String accountNumber, AccountDepositRequest request) {
    // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
    Account account = accountReader.getOwnedAccount(accountNumber);

    AccountTransaction trx =
        processTransaction(account, request.amount(), TransactionType.ATM_DEPOSIT, true, null);

    return AccountTransactionResponse.of(trx);
  }

  // 송금
  @Transactional
  public TransferResponse transfer(TransferRequest request) {
    // 토큰을 통해 현재 사용자가 송금하려는 계좌의 소유자인지 검증
    Account from = accountReader.getOwnedAccount(request.fromAccountNumber());

    // 받는 계좌는 소유자 검증 없이 계좌번호로 조회
    Account to = accountReader.getByAccountNumber(request.toAccountNumber());

    BigDecimal amount = request.amount();

    // 출금 (보내는 사람)
    AccountTransaction withdrawTx =
        processTransaction(
            from, amount, TransactionType.TRANSFER_SEND, false, to.getAccountNumber());

    // 입금 (받는 사람)
    AccountTransaction depositTx =
        processTransaction(
            to, amount, TransactionType.TRANSFER_RECEIVE, true, from.getAccountNumber());

    return TransferResponse.of(from, to, amount);
  }

  // 카드 결제
  @Transactional
  public CardPaymentResponse payByCard(String accountNumber, CardPaymentRequest request) {
    // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
    Account account = accountReader.getOwnedAccount(accountNumber);

    // 계좌에도 로그 남기기위해 반영
    processTransaction(
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

    return CardPaymentResponse.of(saved);
  }

  public AccountTransactionListResponse getTransactions(
      String accountNumber, LocalDate startDate, LocalDate endDate) {
    // 토큰을 통해 현재 사용자가 소유한 계좌인지 검증
    Account account = accountReader.getOwnedAccount(accountNumber);

    // 거래내역 조회
    List<AccountTransactionResponse> transactions =
        accountTransactionRepository
            .findByAccountAndDateGreaterThanEqualAndDateBefore(
                account, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
            .stream()
            .map(AccountTransactionResponse::of)
            .toList();

    // 응답 DTO 생성
    return AccountTransactionListResponse.builder()
        .accountId(account.getAccountId().getValue())
        .transactions(transactions)
        .build();
  }
}
