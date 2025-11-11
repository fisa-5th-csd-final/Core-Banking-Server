package com.fisa.bank.account.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.entity.id.UserId;

@DisplayName("Account 엔티티 테스트")
class AccountTest {

  @Test
  @DisplayName("계좌 생성 시 초기 잔액은 0이다")
  void createAccountWithZeroBalance() {
    // given
    String accountNumber = "1234567890";
    User user = createTestUser();
    String bankCode = "088";

    // when
    Account account = Account.create(accountNumber, user, bankCode);

    // then
    assertThat(account.getAccountNumber()).isEqualTo(accountNumber);
    assertThat(account.getUser()).isEqualTo(user);
    assertThat(account.getBankCode()).isEqualTo(bankCode);
    assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("입금이 정상적으로 수행된다")
  void deposit() {
    // given
    Account account = Account.create("1234567890", createTestUser(), "088");
    BigDecimal depositAmount = new BigDecimal("10000");

    // when
    account.deposit(depositAmount);

    // then
    assertThat(account.getBalance()).isEqualByComparingTo(depositAmount);
  }

  @Test
  @DisplayName("출금이 정상적으로 수행된다")
  void withdraw() {
    // given
    Account account = Account.create("1234567890", createTestUser(), "088");
    account.deposit(new BigDecimal("10000"));
    BigDecimal withdrawAmount = new BigDecimal("5000");

    // when
    account.withdraw(withdrawAmount);

    // then
    assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("5000"));
  }

  @Test
  @DisplayName("계좌 번호가 정상적으로 설정된다")
  void accountNumberIsSet() {
    // given
    String accountNumber = "9876543210";
    User user = createTestUser();

    // when
    Account account = Account.create(accountNumber, user, "088");

    // then
    assertThat(account.getAccountNumber()).isEqualTo(accountNumber);
  }

  @Test
  @DisplayName("은행 코드가 정상적으로 설정된다")
  void bankCodeIsSet() {
    // given
    String bankCode = "020";

    // when
    Account account = Account.create("1234567890", createTestUser(), bankCode);

    // then
    assertThat(account.getBankCode()).isEqualTo(bankCode);
  }

  @Test
  @DisplayName("계좌 소유자가 정상적으로 설정된다")
  void userIsSet() {
    // given
    User user = createTestUser();

    // when
    Account account = Account.create("1234567890", user, "088");

    // then
    assertThat(account.getUser()).isEqualTo(user);
  }

  private User createTestUser() {
    UserAuth userAuth = UserAuth.create("admin", "encodedPassword123!");

    return User.builder()
        .userId(UserId.of(1L))
        .name("홍길동")
        .address("서울특별시 종로구 사직로 9")
        .birthday(LocalDateTime.of(1990, 1, 1, 0, 0))
        .job("개발자")
        .income(BigInteger.valueOf(50000000))
        .creditLevel(CreditRating.B)
        .customerLevel(CustomerLevel.BRONZE)
        .userAuth(userAuth)
        .build();
  }
}
