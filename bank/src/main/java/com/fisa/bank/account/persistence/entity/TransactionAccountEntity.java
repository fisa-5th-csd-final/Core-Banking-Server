/**
 * TransactionAccountEntity
 *
 * 계좌 거래 내역을 저장하는 엔티티 클래스
 *
 * 계좌(AccountEntity)와 연관,
 * 입금, 출금, 송금 등 계좌 단위의 거래 이력을 관리
 *
 * 주요 필드:
 * - trxAId : 거래 식별자
 * - account : 거래가 발생한 계좌
 * - type : 거래 유형 (입금 / 출금 / 송금 등)
 * - amount : 거래 금액
 * - date : 거래 일시
 * - destinationAccount : 상대 계좌 (송금 시)
 * - isIncome : 입금 여부
 */

package com.fisa.bank.account.persistence.entity;

import com.fisa.bank.account.persistence.enums.TransactionType;
import com.fisa.bank.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionAccountEntity extends BaseEntity {

    @Id
    @Column(name = "trx_a_id", length = 20)
    private String trxAId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "destination_account", length = 20)
    private String destinationAccount;

    @Column(name = "is_income", nullable = false)
    private Boolean isIncome;
    
}
