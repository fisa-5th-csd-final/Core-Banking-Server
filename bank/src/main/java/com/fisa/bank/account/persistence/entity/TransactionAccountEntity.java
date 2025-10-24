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
@Setter
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
