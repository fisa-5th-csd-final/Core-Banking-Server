package com.fisa.bank.account.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fisa.bank.account.application.dto.response.AccountDetailResponse;
import com.fisa.bank.account.application.dto.response.AccountListResponse;
import com.fisa.bank.account.application.dto.response.AccountResponse;
import com.fisa.bank.account.application.exception.AccountNotDeletableException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService 테스트")
class AccountServiceTest {

  @Mock private AccountRepository accountRepository;

  @Mock private AccountReader accountReader;

  @InjectMocks private AccountService accountService;

  private User testUser;
  private Account testAccount;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(accountService, "ourBankCode", "088");

    testUser =
        User.create(
            "주주주", // name
            "서울특별시 강남구 테헤란로 123", // address
            LocalDateTime.of(1999, 5, 20, 0, 0), // birthday
            BigInteger.valueOf(55000000), // income
            "백엔드 개발자", // job
            new UserAuth("zootopia@example.com", "encodedPassword123!") // UserAuth (임의 객체)
            );

    testAccount = Account.create("1234567890", testUser, "088");
    ReflectionTestUtils.setField(testAccount, "accountId", AccountId.of(1L));
  }

  @Test
  @DisplayName("계좌 생성이 정상적으로 수행된다")
  void createAccount() {
    // given
    Long userId = 1L;
    when(accountReader.getUserById(userId)).thenReturn(testUser);
    when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

    // when
    AccountResponse response = accountService.createAccount(userId);

    // then
    assertThat(response).isNotNull();
    assertThat(response.accountNumber()).isEqualTo("1234567890");
    verify(accountReader, times(1)).getUserById(userId);
    verify(accountRepository, times(1)).save(any(Account.class));
  }

  @Test
  @DisplayName("계좌 상세 조회가 정상적으로 수행된다")
  void getAccountDetail() {
    // given
    String accountNumber = "1234567890";
    Long userId = 1L;
    when(accountReader.getAccountByAccountNumber(accountNumber)).thenReturn(testAccount);

    // when
    AccountDetailResponse response = accountService.getAccountDetail(accountNumber);

    // then
    assertThat(response).isNotNull();
    assertThat(response.accountNumber()).isEqualTo(accountNumber);
    verify(accountReader, times(1)).getAccountByAccountNumber(accountNumber);
  }

  @Test
  @DisplayName("유저별 계좌 목록 조회가 정상적으로 수행된다")
  void getAccountsByUserId() {
    // given
    Long userId = 1L;
    Account account1 = Account.create("1234567890", testUser, "088");
    ReflectionTestUtils.setField(account1, "accountId", AccountId.of(1L));

    Account account2 = Account.create("9876543210", testUser, "088");
    ReflectionTestUtils.setField(account2, "accountId", AccountId.of(2L));

    List<Account> accounts = List.of(account1, account2);

    when(accountReader.getUserById(userId)).thenReturn(testUser);
    when(accountRepository.findAllByUser(testUser)).thenReturn(accounts);

    // when
    List<AccountListResponse> responses = accountService.getAccountsByUserId(userId);

    // then
    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).accountNumber()).isEqualTo("1234567890");
    assertThat(responses.get(1).accountNumber()).isEqualTo("9876543210");
    verify(accountReader, times(1)).getUserById(userId);
    verify(accountRepository, times(1)).findAllByUser(testUser);
  }

  @Test
  @DisplayName("잔액이 0인 계좌는 삭제가 정상적으로 수행된다")
  void deleteAccountWithZeroBalance() {
    // given
    String accountNumber = "1234567890";
    Long userId = 1L;
    when(accountReader.getAccountByAccountNumber(accountNumber)).thenReturn(testAccount);

    // when
    accountService.deleteAccount(accountNumber);

    // then
    verify(accountReader, times(1)).getAccountByAccountNumber(accountNumber);
    verify(accountRepository, times(1)).delete(testAccount);
  }

  @Test
  @DisplayName("잔액이 있는 계좌는 삭제할 수 없다")
  void deleteAccountWithBalance() {
    // given
    String accountNumber = "1234567890";
    Long userId = 1L;
    Account accountWithBalance = Account.create("1234567890", testUser, "088");
    accountWithBalance.updateBalance(new BigDecimal("10000"));

    when(accountReader.getAccountByAccountNumber(accountNumber)).thenReturn(accountWithBalance);

    // when & then
    assertThatThrownBy(() -> accountService.deleteAccount(accountNumber))
        .isInstanceOf(AccountNotDeletableException.class);

    verify(accountReader, times(1)).getAccountByAccountNumber(accountNumber);
    verify(accountRepository, never()).delete(any());
  }
}
