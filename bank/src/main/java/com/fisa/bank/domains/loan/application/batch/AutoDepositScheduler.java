package com.fisa.bank.domains.loan.application.batch;

import com.fisa.bank.domains.loan.application.service.AutoDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoDepositScheduler {

    private final AutoDepositService autoDepositService;

    @Scheduled(cron = "0 0 3 * * *")
    public void runAutoDepositBatch() {
        autoDepositService.processAutoDeposits();
    }
}

