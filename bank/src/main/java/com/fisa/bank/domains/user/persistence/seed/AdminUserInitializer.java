package com.fisa.bank.domains.user.persistence.seed;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.user.persistence.entity.CreditRating;
import com.fisa.bank.domains.user.persistence.entity.CustomerLevel;
import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

/**
 * 애플리케이션 구동 시 관리자 계정을 보장하는 시드 러너.
 *
 * <p>비밀번호는 환경변수 ADMIN_SEED_PASSWORD가 우선, 없으면 기본값 Admin123! 을 사용한다.
 */
@Component
@Profile({"local", "dev", "prod"})
public class AdminUserInitializer implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
  private static final String DEFAULT_LOGIN_ID = "admin";
  private static final String DEFAULT_PASSWORD = "Admin123!";

  private final UserAuthRepository userAuthRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminUserInitializer(
      UserAuthRepository userAuthRepository,
      UserRepository userRepository,
      @Qualifier("BcryptPasswordEncoder") PasswordEncoder passwordEncoder) {
    this.userAuthRepository = userAuthRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (userAuthRepository.existsById(DEFAULT_LOGIN_ID)) {
      return; // 이미 존재하면 아무 것도 하지 않음
    }

    String rawPassword =
        System.getenv().getOrDefault("ADMIN_SEED_PASSWORD", DEFAULT_PASSWORD).trim();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    UserAuth adminAuth = UserAuth.createAdmin(DEFAULT_LOGIN_ID, encodedPassword);

    User adminUser =
        User.builder()
            .name("관리자")
            .address("Seoul")
            .birthday(LocalDateTime.of(1990, 1, 1, 0, 0))
            .job("ADMIN")
            .income(BigInteger.valueOf(1_000_000_000L))
            .creditLevel(CreditRating.AAA)
            .customerLevel(CustomerLevel.VIP)
            .userAuth(adminAuth)
            .build();

    // 먼저 인증 엔티티를 저장한 뒤 User를 저장
    userAuthRepository.save(adminAuth);
    userRepository.save(adminUser);
    log.info("Seeded admin account with loginId='{}'", DEFAULT_LOGIN_ID);
  }
}
