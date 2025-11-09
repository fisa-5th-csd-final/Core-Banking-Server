package com.fisa.bank.account.application.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import com.fisa.bank.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.account.application.exception.InvalidTransferTargetException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.AccountTransaction;
import com.fisa.bank.account.persistence.entity.CardTransaction;
import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.account.persistence.repository.AccountTransactionRepository;
import com.fisa.bank.account.persistence.repository.CardTransactionRepository;
import com.fisa.bank.common.aop.annotation.DomainType;
import com.fisa.bank.common.aop.annotation.VerifyOwner;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

  private final AccountTransactionRepository accountTransactionRepository;
  private final CardTransactionRepository cardTransactionRepository;
  private final AccountRepository accountRepository;
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
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  @Transactional
  public AccountTransactionResponse withdraw(String accountNumber, AccountWithdrawRequest request) {
    Account account = accountReader.getAccountByAccountNumberWithLock(accountNumber);

    AccountTransaction trx =
        recordTransaction(account, request.amount(), TransactionType.ATM_WITHDRAW, false, null);

    return AccountTransactionResponse.from(trx);
  }

  // 입금
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  @Transactional
  public AccountTransactionResponse deposit(String accountNumber, AccountDepositRequest request) {
    Account account = accountReader.getAccountByAccountNumberWithLock(accountNumber);

    AccountTransaction trx =
        recordTransaction(account, request.amount(), TransactionType.ATM_DEPOSIT, true, null);

    return AccountTransactionResponse.from(trx);
  }

  // 송금
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "fromAccountNumber")
  @Transactional
  public TransferResponse transfer(TransferRequest request) {
    String fromNo = request.fromAccountNumber();
    String toNo = request.toAccountNumber();
    BigDecimal amount = request.amount();

    if (ourBankCode.equals(request.toBankCode()) && fromNo.equals(toNo)) {
      throw new InvalidTransferTargetException();
    }

    if (ourBankCode.equals(request.toBankCode())) {
      var pair = accountReader.lockTransferAccounts(fromNo, toNo);
      Account from = pair.from();
      Account to = pair.to();

      recordTransaction(from, amount, TransactionType.TRANSFER_SEND, false, to.getAccountNumber());
      recordTransaction(
          to, amount, TransactionType.TRANSFER_RECEIVE, true, from.getAccountNumber());

      return TransferResponse.of(from, to, amount);
    } else {
      Account from = accountReader.getAccountByAccountNumberWithLock(fromNo);
      recordTransaction(from, amount, TransactionType.EXTERNAL_TRANSFER_SEND, false, toNo);
      return TransferResponse.ofExternal(from, toNo, amount);
    }
  }

  // 카드 결제
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  @Transactional
  public CardPaymentResponse payByCard(String accountNumber, CardPaymentRequest request) {
    Account account = accountReader.getAccountByAccountNumberWithLock(accountNumber);

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

  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public AccountTransactionListResponse getTransactions(
      String accountNumber, LocalDate startDate, LocalDate endDate) {
    Account account = accountReader.getAccountByAccountNumber(accountNumber);

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
