package com.fisa.bank.domains.loan.persistence.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fisa.bank.domains.loan.persistence.repository.PreferInterestRepository;
import com.fisa.bank.domains.user.persistence.entity.CreditRating;
import com.fisa.bank.domains.user.persistence.entity.CustomerLevel;

/*
   우대 금리 엔티티에 데이터 초기화하는 클래스
   나중에 고객 등급과 신용 등급이 변경됐을 때를 고려해서 Map<복합키, 우대금리>로 구현했습니다.
*/
@Component
@Profile({"local", "dev", "prod"})
public class PreferInterestInitializer implements CommandLineRunner {

  private final PreferInterestRepository preferInterestRepository;

  public PreferInterestInitializer(PreferInterestRepository preferInterestRepository) {
    this.preferInterestRepository = preferInterestRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    // 복합키(고객 등급, 신용 등급)와 우대 금리 쌍을 담을 해시맵
    Map<PreferInterestCompositeKey, BigDecimal> initialData = new HashMap<>();

    // CreditRating: AAA/AA/A/B/C/D
    // CustomerLevel: VVIP/VIP/GOLD/SILVER/BRONZE
    // AAA
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AAA, CustomerLevel.VVIP),
        new BigDecimal("1.5"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AAA, CustomerLevel.VIP), new BigDecimal("1.3"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AAA, CustomerLevel.GOLD),
        new BigDecimal("1.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AAA, CustomerLevel.SILVER),
        new BigDecimal("0.7"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AAA, CustomerLevel.BRONZE),
        new BigDecimal("0.5"));

    // AA
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AA, CustomerLevel.VVIP), new BigDecimal("1.2"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AA, CustomerLevel.VIP), new BigDecimal("1.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AA, CustomerLevel.GOLD), new BigDecimal("0.8"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AA, CustomerLevel.SILVER),
        new BigDecimal("0.5"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.AA, CustomerLevel.BRONZE),
        new BigDecimal("0.3"));

    // A
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.A, CustomerLevel.VVIP), new BigDecimal("1.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.A, CustomerLevel.VIP), new BigDecimal("0.8"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.A, CustomerLevel.GOLD), new BigDecimal("0.5"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.A, CustomerLevel.SILVER),
        new BigDecimal("0.3"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.A, CustomerLevel.BRONZE),
        new BigDecimal("0.1"));

    // B
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.B, CustomerLevel.VVIP), new BigDecimal("0.8"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.B, CustomerLevel.VIP), new BigDecimal("0.5"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.B, CustomerLevel.GOLD), new BigDecimal("0.3"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.B, CustomerLevel.SILVER),
        new BigDecimal("0.1"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.B, CustomerLevel.BRONZE),
        new BigDecimal("0.0"));

    // C
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.C, CustomerLevel.VVIP), new BigDecimal("0.5"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.C, CustomerLevel.VIP), new BigDecimal("0.3"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.C, CustomerLevel.GOLD), new BigDecimal("0.1"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.C, CustomerLevel.SILVER),
        new BigDecimal("0.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.C, CustomerLevel.BRONZE),
        new BigDecimal("0.0"));

    // D
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.D, CustomerLevel.VVIP), new BigDecimal("0.3"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.D, CustomerLevel.VIP), new BigDecimal("0.1"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.D, CustomerLevel.GOLD), new BigDecimal("0.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.D, CustomerLevel.SILVER),
        new BigDecimal("0.0"));
    initialData.put(
        new PreferInterestCompositeKey(CreditRating.D, CustomerLevel.BRONZE),
        new BigDecimal("0.0"));

    // 우대금리 엔티티를 담을 리스트
    List<PreferInterest> preferInterestList = new ArrayList<>();

    // 복합키와 우대금리 쌍을 엔티티로 변환하는 코드
    for (Map.Entry<PreferInterestCompositeKey, BigDecimal> entry : initialData.entrySet()) {
      PreferInterest pi = new PreferInterest(entry.getKey(), entry.getValue());
      preferInterestList.add(pi);
    }

    // DB에 초기화값 세팅
    preferInterestRepository.saveAll(preferInterestList);
  }
}
