package com.fisa.bank.user.persistence.entity;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class User extends BaseEntity {

    // TODO: Id 생성기 적용
    @Id
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime birthday;

    @Column(nullable = false)
    private String job;

    @Column(nullable = false)
    private BigInteger income;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CreditRating creditLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerLevel customerLevel;

    @OneToOne(cascade = CascadeType.REMOVE)
    private UserAuth userAuth;

    public static User create(
            String userId,
            String name,
            String address,
            LocalDateTime birthday,
            BigInteger income,
            String loginId,
            String password
    ){
        return User.builder()
                .userId(userId)
                .name(name)
                .address(address)
                .birthday(birthday)
                .income(income)
                .creditLevel(CreditRating.B)
                .customerLevel(CustomerLevel.BRONZE)
                .userAuth(UserAuth.create(loginId, password))
                .build();
    }

}
