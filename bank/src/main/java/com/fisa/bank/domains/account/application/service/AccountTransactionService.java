package com.fisa.bank.domains.account.application.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.domains.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.domains.account.application.dto.request.CardPaymentRequest;
import com.fisa.bank.domains.account.application.dto.request.TransferRequest;
import com.fisa.bank.domains.account.application.dto.response.AccountTransactionListResponse;
import com.fisa.bank.domains.account.application.dto.response.AccountTransactionResponse;
import com.fisa.bank.domains.account.application.dto.response.CardPaymentResponse;
import com.fisa.bank.domains.account.application.dto.response.TransferResponse;
import com.fisa.bank.domains.account.application.exception.InvalidTransferTargetException;
import com.fisa.bank.domains.account.application.service.reader.AccountReader;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.entity.CardTransaction;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.account.persistence.repository.AccountTransactionRepository;
import com.fisa.bank.domains.account.persistence.repository.CardTransactionRepository;
import com.fisa.bank.domains.common.aop.annotation.DomainType;
import com.fisa.bank.domains.common.aop.annotation.VerifyOwner;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

  private final AccountReader accountReader;
  private final AccountDomainRecoder accountDomainManager;
  private final CardTransactionRepository cardTransactionRepository;
  private final AccountTransactionRepository accountTransactionRepository;

  @Value("${bank.code}")
  private String ourBankCode;

  @Transactional
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public AccountTransactionResponse withdraw(String accountNumber, AccountWithdrawRequest request) {
    Account account = accountReader.getAccountByAccountNumberWithLock(accountNumber);
    var trx =
        accountDomainManager.record(
            account, request.amount(), TransactionType.ATM_WITHDRAW, false, null);
    return AccountTransactionResponse.from(trx);
  }

  @Transactional
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public AccountTransactionResponse deposit(String accountNumber, AccountDepositRequest request) {
    Account account = accountReader.getAccountByAccountNumberWithLock(accountNumber);
    var trx =
        accountDomainManager.record(
            account, request.amount(), TransactionType.ATM_DEPOSIT, true, null);
    return AccountTransactionResponse.from(trx);
  }

  @Transactional
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "fromAccountNumber")
  public TransferResponse transfer(TransferRequest request) {
    if (ourBankCode.equals(request.toBankCode())
        && request.fromAccountNumber().equals(request.toAccountNumber())) {
      throw new InvalidTransferTargetException();
    }

    if (ourBankCode.equals(request.toBankCode())) {
      var pair =
          accountReader.lockTransferAccounts(
              request.fromAccountNumber(), request.toAccountNumber());
      var from = pair.from();
      var to = pair.to();

      accountDomainManager.record(
          from, request.amount(), TransactionType.TRANSFER_SEND, false, to.getAccountNumber());
      accountDomainManager.record(
          to, request.amount(), TransactionType.TRANSFER_RECEIVE, true, from.getAccountNumber());

      return TransferResponse.of(from, to, request.amount());
    } else {
      var from = accountReader.getAccountByAccountNumberWithLock(request.fromAccountNumber());
      accountDomainManager.record(
          from,
          request.amount(),
          TransactionType.EXTERNAL_TRANSFER_SEND,
          false,
          request.toAccountNumber());
      return TransferResponse.ofExternal(from, request.toAccountNumber(), request.amount());
    }
  }

  @Transactional
  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public CardPaymentResponse payByCard(String accountNumber, CardPaymentRequest request) {
    var account = accountReader.getAccountByAccountNumberWithLock(accountNumber);

    accountDomainManager.record(
        account, request.amount(), TransactionType.CARD_PAYMENT, false, request.storeName());

    var cardTrx =
        CardTransaction.builder()
            .account(account)
            .amount(request.amount())
            .storeName(request.storeName())
            .category(request.category())
            .build();

    return CardPaymentResponse.from(cardTransactionRepository.save(cardTrx));
  }

  @VerifyOwner(domain = DomainType.ACCOUNT, idParam = "accountNumber")
  public AccountTransactionListResponse getTransactions(
      String accountNumber, LocalDate startDate, LocalDate endDate) {
    Account account = accountReader.getAccountByAccountNumber(accountNumber);

    // 거래내역 조회
    List<AccountTransactionResponse> transactions =
        accountTransactionRepository
            .findByAccountAndCreatedAtGreaterThanEqualAndCreatedAtBefore(
                account, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
            .stream()
            .map(AccountTransactionResponse::from)
            .toList();

    // 응답 DTO 생성
    return new AccountTransactionListResponse(
        account.getAccountId().getValue(), account.getAccountNumber(), transactions);
  }
}
