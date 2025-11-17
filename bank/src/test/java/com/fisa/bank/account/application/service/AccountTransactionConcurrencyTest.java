package com.fisa.bank.account.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fisa.bank.domains.account.application.dto.request.AccountDepositRequest;
import com.fisa.bank.domains.account.application.dto.request.AccountWithdrawRequest;
import com.fisa.bank.domains.account.application.dto.request.TransferRequest;
import com.fisa.bank.domains.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.domains.account.application.service.AccountTransactionService;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.account.persistence.repository.AccountRepository;
import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

@SpringBootTest(properties = {"bank.code=020"})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:env.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("AccountTransaction 동시성 제어 테스트")
class AccountTransactionConcurrencyTest {

  @Autowired private AccountTransactionService accountTransactionService;
  @Autowired private AccountRepository accountRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserAuthRepository userAuthRepository;

  private Account account1;
  private Account account2;
  private User testUser;

  @BeforeEach
  void setUp() throws Exception {
    // UserAuth 먼저 저장
    UserAuth userAuth = new UserAuth("zootopia@example.com", "encodedPassword123!");
    userAuth = userAuthRepository.saveAndFlush(userAuth);

    // User 생성 및 저장
    testUser =
        User.create(
            "주주주", // name
            "서울특별시 강남구 테헤란로 123", // address
            LocalDateTime.of(1999, 5, 20, 0, 0), // birthday
            BigInteger.valueOf(55000000), // income
            "백엔드 개발자", // job
            userAuth // 영속된 UserAuth 참조
            );
    testUser = userRepository.saveAndFlush(testUser);

    account1 = Account.create("1234567890", testUser, "088");
    account1.deposit(new BigDecimal("1000000"));

    account2 = Account.create("9876543210", testUser, "088");
    account2.deposit(new BigDecimal("1000000"));

    accountRepository.saveAndFlush(account1);
    accountRepository.saveAndFlush(account2);

    accountRepository.flush();
  }

  /* 공통 동시성 제어 실행 메서드 */
  private void runConcurrentTasks(int threadCount, Runnable task) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.execute(
          () -> {
            try {
              task.run();
            } finally {
              latch.countDown();
            }
          });
    }

    latch.await();
    executorService.shutdown();
  }

  @Test
  @DisplayName("동시에 여러 건의 출금이 발생해도 잔액이 정확하게 유지된다")
  void concurrentWithdrawals() throws InterruptedException {
    int threadCount = 10;
    BigDecimal withdrawAmount = new BigDecimal("10000");
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    runConcurrentTasks(
        threadCount,
        () -> {
          try {
            AccountWithdrawRequest request = new AccountWithdrawRequest(withdrawAmount);
            accountTransactionService.withdraw(account1.getAccountNumber(), request);
            successCount.incrementAndGet();
          } catch (InsufficientBalanceException e) {
            failCount.incrementAndGet();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    Account updatedAccount =
        accountRepository.findByAccountNumber(account1.getAccountNumber()).orElseThrow();
    BigDecimal expectedBalance =
        new BigDecimal("1000000")
            .subtract(withdrawAmount.multiply(new BigDecimal(successCount.get())));

    assertThat(updatedAccount.getBalance()).isEqualByComparingTo(expectedBalance);
    assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
  }

  @Test
  @DisplayName("동시에 여러 건의 입금이 발생해도 잔액이 정확하게 유지된다")
  void concurrentDeposits() throws InterruptedException {
    int threadCount = 10;
    BigDecimal depositAmount = new BigDecimal("10000");
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    runConcurrentTasks(
        threadCount,
        () -> {
          try {
            AccountDepositRequest request = new AccountDepositRequest(depositAmount);
            accountTransactionService.deposit(account1.getAccountNumber(), request);
            successCount.incrementAndGet();
          } catch (Exception e) {
            failCount.incrementAndGet();
            org.junit.jupiter.api.Assertions.fail("예상치 못한 예외 발생", e);
          }
        });

    Account updatedAccount =
        accountRepository.findByAccountNumber(account1.getAccountNumber()).orElseThrow();
    BigDecimal expectedBalance =
        new BigDecimal("1000000").add(depositAmount.multiply(new BigDecimal(threadCount)));

    assertThat(updatedAccount.getBalance()).isEqualByComparingTo(expectedBalance);
  }

  @Test
  @DisplayName("동시에 입출금이 섞여 발생해도 잔액이 정확하게 유지된다")
  void concurrentMixedTransactions() throws InterruptedException {
    int threadCount = 20;
    BigDecimal amount = new BigDecimal("5000");
    AtomicInteger depositCount = new AtomicInteger(0);
    AtomicInteger withdrawCount = new AtomicInteger(0);

    runConcurrentTasks(
        threadCount,
        () -> {
          try {
            int index = (int) (Thread.currentThread().getId() % 2);
            if (index == 0) {
              AccountDepositRequest request = new AccountDepositRequest(amount);
              accountTransactionService.deposit(account1.getAccountNumber(), request);
              depositCount.incrementAndGet();
            } else {
              AccountWithdrawRequest request = new AccountWithdrawRequest(amount);
              accountTransactionService.withdraw(account1.getAccountNumber(), request);
              withdrawCount.incrementAndGet();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    Account updatedAccount =
        accountRepository.findByAccountNumber(account1.getAccountNumber()).orElseThrow();

    BigDecimal expectedBalance =
        new BigDecimal("1000000")
            .add(amount.multiply(new BigDecimal(depositCount.get())))
            .subtract(amount.multiply(new BigDecimal(withdrawCount.get())));

    assertThat(updatedAccount.getBalance()).isEqualByComparingTo(expectedBalance);
  }

  @Test
  @DisplayName("동시에 여러 건의 송금이 발생해도 데드락 없이 정상 처리된다")
  void concurrentTransfersWithoutDeadlock() throws InterruptedException {
    int threadCount = 10;
    BigDecimal transferAmount = new BigDecimal("10000");
    AtomicInteger successCount = new AtomicInteger(0);

    runConcurrentTasks(
        threadCount,
        () -> {
          try {
            TransferRequest request =
                new TransferRequest(
                    account1.getAccountNumber(),
                    account2.getAccountNumber(),
                    "020",
                    transferAmount);
            accountTransactionService.transfer(request);
            successCount.incrementAndGet();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    Account updatedAccount1 =
        accountRepository.findByAccountNumber(account1.getAccountNumber()).orElseThrow();
    Account updatedAccount2 =
        accountRepository.findByAccountNumber(account2.getAccountNumber()).orElseThrow();

    BigDecimal expectedBalance1 =
        new BigDecimal("1000000")
            .subtract(transferAmount.multiply(new BigDecimal(successCount.get())));
    BigDecimal expectedBalance2 =
        new BigDecimal("1000000").add(transferAmount.multiply(new BigDecimal(successCount.get())));

    assertThat(updatedAccount1.getBalance()).isEqualByComparingTo(expectedBalance1);
    assertThat(updatedAccount2.getBalance()).isEqualByComparingTo(expectedBalance2);
  }

  @Test
  @DisplayName("잔액 부족 시 동시 출금이 올바르게 제어된다")
  void concurrentWithdrawalsWithInsufficientBalance() throws InterruptedException {
    Account poorAccount = Account.create("1111111111", testUser, "088");
    poorAccount.deposit(new BigDecimal("50000"));
    accountRepository.save(poorAccount);

    int threadCount = 10;
    BigDecimal withdrawAmount = new BigDecimal("10000");
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    runConcurrentTasks(
        threadCount,
        () -> {
          try {
            AccountWithdrawRequest request = new AccountWithdrawRequest(withdrawAmount);
            accountTransactionService.withdraw(poorAccount.getAccountNumber(), request);
            successCount.incrementAndGet();
          } catch (InsufficientBalanceException e) {
            failCount.incrementAndGet();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    Account updatedAccount =
        accountRepository.findByAccountNumber(poorAccount.getAccountNumber()).orElseThrow();

    assertThat(successCount.get()).isLessThanOrEqualTo(5);
    assertThat(failCount.get()).isGreaterThan(0);
    assertThat(updatedAccount.getBalance()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
  }
}
