/**
 * TransactionAccountEntity
 *
 * <p>계좌 거래 내역을 저장하는 엔티티 클래스
 *
 * <p>계좌(AccountEntity)와 연관, 입금, 출금, 송금 등 계좌 단위의 거래 이력을 관리
 *
 * <p>주요 필드: - trxAId : 거래 식별자 - account : 거래가 발생한 계좌 - type : 거래 유형 (입금 / 출금 / 송금 등) - amount : 거래
 * 금액 - date : 거래 일시 - destinationAccount : 상대 계좌 (송금 시) - isIncome : 입금 여부
 */
package com.fisa.bank.domains.account.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fisa.bank.domains.account.persistence.entity.id.AccountTransactionId;
import com.fisa.bank.domains.account.persistence.entity.id.AccountTransactionIdJavaType;
import com.fisa.bank.domains.account.persistence.enums.TransactionType;
import com.fisa.bank.domains.common.persistence.entity.BaseEntity;

@Entity
@Table(name = "transaction_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransaction extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JavaType(AccountTransactionIdJavaType.class)
  @JdbcTypeCode(SqlTypes.BIGINT)
  private AccountTransactionId trxAId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private TransactionType type;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "balance_before", nullable = false)
  private BigDecimal balanceBefore; // 거래 전 잔액

  @Column(name = "balance_after", nullable = false)
  private BigDecimal balanceAfter; // 거래 후 잔액

  @Column(name = "destination_account", length = 20)
  private String destinationAccount;

  @Column(name = "is_income", nullable = false)
  private Boolean isIncome;
}
