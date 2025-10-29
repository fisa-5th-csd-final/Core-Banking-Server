package com.fisa.bank.common.config;

import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import com.fisa.bank.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("✅ 기본 데이터 이미 존재함. 초기화 스킵.");
            return;
        }

        System.out.println("🚀 [DataInitializer] 테스트용 유저 및 계좌 데이터 생성 시작...");

        // 1️⃣ UserAuth 생성
        UserAuth authMink = UserAuth.create("mink", "1234");
        UserAuth authZootopia = UserAuth.create("zootopia", "5678");

        userAuthRepository.save(authMink);
        userAuthRepository.save(authZootopia);

        // 2️⃣ User 생성 (User.create 사용)
        User mink = User.create(
                "김민경",
                "서울특별시 강남구 테헤란로 152",
                LocalDateTime.of(2000, 1, 1, 0, 0),
                BigInteger.valueOf(5200000),
                "백엔드 개발자",
                authMink
        );

        User zootopia = User.create(
                "주토피아",
                "서울특별시 송파구 가락로 90",
                LocalDateTime.of(2000, 3, 15, 0, 0),
                BigInteger.valueOf(4300000),
                "UX 디자이너",
                authZootopia
        );

        userRepository.save(mink);
        userRepository.save(zootopia);

        // 3️⃣ Account 생성
        Account account1 = Account.builder()
                .accountNumber("1001-01-000001")
                .bankCode("001")
                .user(mink)
                .balance(BigDecimal.valueOf(1_000_000))
                .build();

        Account account2 = Account.builder()
                .accountNumber("1001-01-000002")
                .bankCode("001")
                .user(zootopia)
                .balance(BigDecimal.valueOf(2_000_000))
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        System.out.println("✅ [DataInitializer] 테스트용 유저 및 계좌 데이터 생성 완료!");
    }
}
