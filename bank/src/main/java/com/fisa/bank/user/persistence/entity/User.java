package com.fisa.bank.user.persistence.entity;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.entity.id.UserIdJavaType;
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
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JavaType(UserIdJavaType.class)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private UserId userId;

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
            String name,
            String address,
            LocalDateTime birthday,
            BigInteger income,
            String job,
            UserAuth userAuth
    ){
        return User.builder()
                .name(name)
                .address(address)
                .birthday(birthday)
                .income(income)
                .job(job)
                .creditLevel(CreditRating.B)
                .customerLevel(CustomerLevel.BRONZE)
                .userAuth(userAuth)
                .build();
    }

}
