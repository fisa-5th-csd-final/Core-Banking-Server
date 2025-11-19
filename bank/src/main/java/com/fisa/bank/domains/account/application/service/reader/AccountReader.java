package com.fisa.bank.domains.account.application.service.reader;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fisa.bank.domains.account.application.exception.AccountNotFoundException;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.user.application.service.reader.UserReader;
import com.fisa.bank.domains.user.persistence.entity.User;

@Component
@RequiredArgsConstructor
public class AccountReader {

  private final AccountRepository accountRepository;
  private final UserReader userReader;

  public Account getAccountByAccountNumber(String accountNumber) {
    return accountRepository
        .findByAccountNumber(accountNumber)
        .orElseThrow(AccountNotFoundException::new);
  }

  // 계좌번호로 조회 (비관적 락)
  public Account getAccountByAccountNumberWithLock(String accountNumber) {
    return accountRepository
        .findByAccountNumberWithLock(accountNumber)
        .orElseThrow(AccountNotFoundException::new);
  }

  // 사용자 가져오기
  public User getUserById(Long userId) {
    return userReader.getUserById(userId);
  }

  // 송금용 두 계좌 조회 (데드락 방지를 위해 정렬된 순서로 락 획득)
  public TransferAccountsPair lockTransferAccounts(
      String fromAccountNumber, String toAccountNumber) {
    List<String> ordered = List.of(fromAccountNumber, toAccountNumber).stream().sorted().toList();

    List<Account> locked = accountRepository.lockTwoAccountsByNumbers(ordered);

    Account from =
        locked.stream()
            .filter(a -> a.getAccountNumber().equals(fromAccountNumber))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("송금하는 계좌를 찾을 수 없습니다"));

    Account to =
        locked.stream()
            .filter(a -> a.getAccountNumber().equals(toAccountNumber))
            .findFirst()
            .orElseThrow(() -> new AccountNotFoundException("수취 계좌를 찾을 수 없습니다."));

    return new TransferAccountsPair(from, to);
  }

  // 송금용 계좌 쌍을 담는 record
  public record TransferAccountsPair(Account from, Account to) {}
}
