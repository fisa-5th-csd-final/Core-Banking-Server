package com.fisa.bank.account.persistence.entity;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity extends BaseEntity {
    @Id
    @Column(name = "account_id", length = 20)
    private String accountId;

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "account_number", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Column(name = "account_balance", nullable = false)
    private BigDecimal accountBalance;

    // TODO: 추후 UserEntity와 합칠 때 작성
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
     */

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 함");
        }
        this.accountBalance = this.accountBalance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 함");
        }
        if (this.accountBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("잔액 부족");
        }
        this.accountBalance = this.accountBalance.subtract(amount);
    }
}
