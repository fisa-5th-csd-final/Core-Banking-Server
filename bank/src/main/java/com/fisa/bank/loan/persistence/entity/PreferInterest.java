package com.fisa.bank.loan.persistence.entity;

import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 우대 금리 매핑 테이블 - 신용 등급, 고객 등급에 따라 고정된 값 반환
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferInterest {

    @EmbeddedId
    private PreferInterestCompositeKey id;

    // 우대금리
    @Column(nullable = false)
    private BigDecimal preferInterest;

    // 복합키와 우대 금리 초기화 생성자
    public PreferInterest(PreferInterestCompositeKey id, BigDecimal preferInterest){
        this.id = id;
        this.preferInterest = preferInterest;
    }
}
