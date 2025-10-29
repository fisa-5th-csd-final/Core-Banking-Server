/**
 * TransactionCardEntity
 *
 * 카드 거래 내역을 저장하는 엔티티 클래스
 *
 * 계좌(AccountEntity)와의 연관관계를 통해
 * 카드 결제 내역, 결제 금액, 가맹점, 소비 카테고리 등의 정보를 관리
 *
 * 주요 필드:
 * - trxCId : 카드 거래 식별자
 * - account : 결제가 발생한 계좌
 * - amount : 결제 금액
 * - storeName : 가맹점명
 * - category : 소비 카테고리 (예: FOOD, TRANSPORT, SHOPPING, ETC)
 */

package com.fisa.bank.account.persistence.entity;

import com.fisa.bank.account.persistence.enums.ConsumptionCategory;
import com.fisa.bank.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_card")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardTransaction extends BaseEntity {

    @Id
    @Column(name = "trx_c_id", length = 20)
    private String trxCId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "store_name", length = 50)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private ConsumptionCategory category;  // 예: FOOD, TRANSPORT, SHOPPING, ETC
}
