package com.fisa.bank.account.persistence.entity;

import com.fisa.bank.account.persistence.enums.ConsumptionCategory;
import com.fisa.bank.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCardEntity extends BaseEntity {

    @Id
    @Column(name = "trx_c_id", length = 20)
    private String trxCId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "store_name", length = 50)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private ConsumptionCategory category;  // 예: FOOD, TRANSPORT, SHOPPING, ETC
}
