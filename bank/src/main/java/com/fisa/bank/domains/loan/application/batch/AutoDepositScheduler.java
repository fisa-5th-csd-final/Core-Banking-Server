package com.fisa.bank.domains.loan.application.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.loan.application.service.AutoDepositService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoDepositScheduler {

  private final AutoDepositService autoDepositService;

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  @Transactional
  public void runAutoDepositBatch() {
    try {
      autoDepositService.processAutoDeposits();
      log.info("스케줄러 실행됨!");
    } catch (Exception ex) {
      log.error("자동예치 스케줄러 오류 발생: {}", ex.getMessage(), ex);
    }
  }
}
