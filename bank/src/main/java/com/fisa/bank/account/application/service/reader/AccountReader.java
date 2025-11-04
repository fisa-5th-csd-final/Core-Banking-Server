package com.fisa.bank.account.application.service.reader;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fisa.bank.account.application.exception.AccessDeniedException;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.user.application.service.UserService;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;

@Component
@RequiredArgsConstructor
public class AccountReader {

  private final AccountRepository accountRepository;
  private final UserService userService;

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

  // 현재 로그인한 사용자가 계좌의 소유자인지 검증 후, 계좌 반환
  public Account getOwnedAccount(String accountNumber, Long userId) {
    Account account = getAccountByAccountNumber(accountNumber);
    Long accountOwnerId = account.getUser().getUserId().getValue();

    if (!accountOwnerId.equals(userId)) {
      throw new AccessDeniedException();
    }
    return account;
  }

  // 현재 로그인한 사용자가 계좌의 소유자인지 검증 후, 계좌 반환 (비관적 락)
  public Account getOwnedAccountWithLock(String accountNumber, Long userId) {
    Account account = getAccountByAccountNumberWithLock(accountNumber);
    Long accountOwnerId = account.getUser().getUserId().getValue();

    if (!accountOwnerId.equals(userId)) {
      throw new AccessDeniedException();
    }
    return account;
  }

  // 사용자 가져오기
  public User getUserById(Long userId) {
    return userService.getUserById(UserId.of(userId));
  }

  // 송금용 두 계좌 조회 (데드락 방지를 위해 정렬된 순서로 락 획득)
  public TransferAccountsPair lockTransferAccounts(
      String fromAccountNumber, String toAccountNumber, Long userId) {
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

    Long accountOwnerId = from.getUser().getUserId().getValue();
    if (!accountOwnerId.equals(userId)) {
      throw new AccessDeniedException();
    }

    return new TransferAccountsPair(from, to);
  }

  // 송금용 계좌 쌍을 담는 record
  public record TransferAccountsPair(Account from, Account to) {}
}
