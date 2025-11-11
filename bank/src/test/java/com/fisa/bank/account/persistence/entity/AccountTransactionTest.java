package com.fisa.bank.account.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.entity.id.UserId;

@DisplayName("AccountTransaction 엔티티 테스트")
class AccountTransactionTest {

  @Test
  @DisplayName("입금 거래 내역이 정상적으로 생성된다")
  void createDepositTransaction() {
    // given
    Account account = createTestAccount();
    BigDecimal amount = new BigDecimal("10000");
    BigDecimal balanceBefore = BigDecimal.ZERO;
    BigDecimal balanceAfter = new BigDecimal("10000");
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.ATM_DEPOSIT)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .isIncome(true)
            .build();

    // then
    assertThat(transaction.getAccount()).isEqualTo(account);
    assertThat(transaction.getType()).isEqualTo(TransactionType.ATM_DEPOSIT);
    assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(balanceBefore);
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(balanceAfter);
    assertThat(transaction.getIsIncome()).isTrue();
    assertThat(transaction.getDestinationAccount()).isNull();
  }

  @Test
  @DisplayName("출금 거래 내역이 정상적으로 생성된다")
  void createWithdrawTransaction() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("50000"));
    BigDecimal amount = new BigDecimal("10000");
    BigDecimal balanceBefore = new BigDecimal("50000");
    BigDecimal balanceAfter = new BigDecimal("40000");
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.ATM_WITHDRAW)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .isIncome(false)
            .build();

    // then
    assertThat(transaction.getAccount()).isEqualTo(account);
    assertThat(transaction.getType()).isEqualTo(TransactionType.ATM_WITHDRAW);
    assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(balanceBefore);
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(balanceAfter);
    assertThat(transaction.getIsIncome()).isFalse();
    assertThat(transaction.getDestinationAccount()).isNull();
  }

  @Test
  @DisplayName("송금 거래 내역이 상대 계좌 정보와 함께 생성된다")
  void createTransferTransaction() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("100000"));
    BigDecimal amount = new BigDecimal("30000");
    BigDecimal balanceBefore = new BigDecimal("100000");
    BigDecimal balanceAfter = new BigDecimal("70000");
    String destinationAccount = "9876543210";
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.TRANSFER_SEND)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .destinationAccount(destinationAccount)
            .isIncome(false)
            .build();

    // then
    assertThat(transaction.getAccount()).isEqualTo(account);
    assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER_SEND);
    assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(balanceBefore);
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(balanceAfter);
    assertThat(transaction.getDestinationAccount()).isEqualTo(destinationAccount);
    assertThat(transaction.getIsIncome()).isFalse();
  }

  @Test
  @DisplayName("송금 수신 거래 내역이 정상적으로 생성된다")
  void createTransferReceiveTransaction() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("50000"));
    BigDecimal amount = new BigDecimal("20000");
    BigDecimal balanceBefore = new BigDecimal("50000");
    BigDecimal balanceAfter = new BigDecimal("70000");
    String sourceAccount = "1234567890";
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.TRANSFER_RECEIVE)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .destinationAccount(sourceAccount)
            .isIncome(true)
            .build();

    // then
    assertThat(transaction.getAccount()).isEqualTo(account);
    assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER_RECEIVE);
    assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(balanceBefore);
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(balanceAfter);
    assertThat(transaction.getDestinationAccount()).isEqualTo(sourceAccount);
    assertThat(transaction.getIsIncome()).isTrue();
  }

  @Test
  @DisplayName("카드 결제 거래 내역이 상점명과 함께 생성된다")
  void createCardPaymentTransaction() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("100000"));
    BigDecimal amount = new BigDecimal("15000");
    BigDecimal balanceBefore = new BigDecimal("100000");
    BigDecimal balanceAfter = new BigDecimal("85000");
    String storeName = "스타벅스 강남점";
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.CARD_PAYMENT)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .destinationAccount(storeName)
            .isIncome(false)
            .build();

    // then
    assertThat(transaction.getAccount()).isEqualTo(account);
    assertThat(transaction.getType()).isEqualTo(TransactionType.CARD_PAYMENT);
    assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(balanceBefore);
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(balanceAfter);
    assertThat(transaction.getDestinationAccount()).isEqualTo(storeName);
    assertThat(transaction.getIsIncome()).isFalse();
  }

  @Test
  @DisplayName("거래 전후 잔액이 정확하게 기록된다")
  void balanceBeforeAndAfterAreRecordedCorrectly() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("1000000"));
    BigDecimal amount = new BigDecimal("250000");
    BigDecimal balanceBefore = new BigDecimal("1000000");
    BigDecimal balanceAfter = balanceBefore.subtract(amount);

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.ATM_WITHDRAW)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .isIncome(false)
            .build();

    // then
    assertThat(transaction.getBalanceBefore()).isEqualByComparingTo(new BigDecimal("1000000"));
    assertThat(transaction.getBalanceAfter()).isEqualByComparingTo(new BigDecimal("750000"));
    assertThat(transaction.getBalanceAfter())
        .isEqualByComparingTo(transaction.getBalanceBefore().subtract(amount));
  }

  @Test
  @DisplayName("타행 송금 거래 내역이 정상적으로 생성된다")
  void createExternalTransferTransaction() {
    // given
    Account account = createTestAccountWithBalance(new BigDecimal("500000"));
    BigDecimal amount = new BigDecimal("100000");
    BigDecimal balanceBefore = new BigDecimal("500000");
    BigDecimal balanceAfter = new BigDecimal("400000");
    String externalAccount = "110-123-456789";
    LocalDateTime transactionDate = LocalDateTime.now();

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.EXTERNAL_TRANSFER_SEND)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .destinationAccount(externalAccount)
            .isIncome(false)
            .build();

    // then
    assertThat(transaction.getType()).isEqualTo(TransactionType.EXTERNAL_TRANSFER_SEND);
    assertThat(transaction.getDestinationAccount()).isEqualTo(externalAccount);
    assertThat(transaction.getIsIncome()).isFalse();
  }

  @Test
  @DisplayName("거래 날짜가 정상적으로 기록된다")
  void transactionDateIsRecorded() {
    // given
    Account account = createTestAccount();
    LocalDateTime specificDate = LocalDateTime.of(2025, 11, 6, 14, 30);

    // when
    AccountTransaction transaction =
        AccountTransaction.builder()
            .account(account)
            .type(TransactionType.ATM_DEPOSIT)
            .amount(new BigDecimal("10000"))
            .balanceBefore(BigDecimal.ZERO)
            .balanceAfter(new BigDecimal("10000"))
            .isIncome(true)
            .build();

    // then
    assertThat(transaction.getCreatedAt()).isNotNull();
    assertThat(transaction.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  private Account createTestAccount() {
    return Account.create("1234567890", createTestUser(), "088");
  }

  private Account createTestAccountWithBalance(BigDecimal balance) {
    Account account = Account.create("1234567890", createTestUser(), "088");
    account.deposit(balance);
    return account;
  }

  private User createTestUser() {
    UserAuth userAuth = UserAuth.create("testuser", "encodedPassword123!");

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
