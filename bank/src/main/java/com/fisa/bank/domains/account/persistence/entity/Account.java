/**
 * AccountEntity
 *
 * <p>은행 계좌 정보를 나타내는 엔티티 클래스입니다. 사용자 ID, 계좌번호, 잔액, 은행 코드 등의 필드를 포함하며, 계좌 생성 및 입출금 관련 비즈니스 로직을 제공
 *
 * <p>주요 필드: - accountId : 계좌 식별자 - accountNumber : 계좌 번호 - userId : 사용자 식별자 - balance : 계좌 잔액 -
 * bankCode : 은행 코드
 *
 * <p>주요 메서드: - create() : 신규 계좌 생성 팩토리 메서드 - deposit() : 입금 처리 - withdraw() : 출금 처리
 */
package com.fisa.bank.domains.account.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.domains.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.domains.account.persistence.entity.id.*;
import com.fisa.bank.domains.account.persistence.entity.id.AccountId;
import com.fisa.bank.domains.account.persistence.entity.id.AccountIdJavaType;
import com.fisa.bank.domains.common.persistence.entity.BaseEntity;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;
import com.fisa.bank.domains.user.persistence.entity.User;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Account extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(AccountIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private AccountId accountId;

  @Column(nullable = false, unique = true)
  private String accountNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false, length = 3)
  private String bankCode;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "account")
  private LoanLedger loanLedger;

  @Builder.Default
  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AccountTransaction> accountTransactions = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CardTransaction> cardTransactions = new ArrayList<>();

  public static Account create(String accountNumber, User user, String bankCode) {
    return Account.builder()
        .accountNumber(accountNumber)
        .user(user)
        .bankCode(bankCode)
        .balance(BigDecimal.ZERO)
        .build();
  }

  public void withdraw(BigDecimal amount) {
    if (balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException();
    }
    this.balance = balance.subtract(amount);
  }

  public void deposit(BigDecimal amount) {
    this.balance = balance.add(amount);
  }
}
