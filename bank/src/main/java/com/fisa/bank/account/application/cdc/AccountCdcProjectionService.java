// package com.fisa.bank.account.application.cdc;
//
// import java.time.Instant;
// import java.util.Map;
// import java.util.Optional;
// import java.util.concurrent.ConcurrentHashMap;
//
// import lombok.extern.slf4j.Slf4j;
//
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Service;
//
// @Slf4j
// @Service
// @ConditionalOnProperty(prefix = "cdc", name = "enabled", havingValue = "true", matchIfMissing =
// true)
//// Account 테이블의 CDC Payload를 받아서, 스냅샷을 반영하는 역할
// public class AccountCdcProjectionService {
//
//  private final Map<Long, AccountCdcPayload> snapshots = new ConcurrentHashMap<>();
//
//  public void upsert(AccountCdcPayload payload, Instant timestamp) {
//    snapshots.put(payload.accountId(), payload);
//    log.debug(
//        "Account CDC 스냅샷 저장 accountId={} balance={} ts={}",
//        payload.accountId(),
//        payload.balance(),
//        timestamp);
//  }
//
//  public void remove(Long accountId, Instant timestamp) {
//    snapshots.remove(accountId);
//    log.debug("Account CDC 스냅샷 삭제 accountId={} ts={}", accountId, timestamp);
//  }
//
//  public Optional<AccountCdcPayload> findCached(Long accountId) {
//    return Optional.ofNullable(snapshots.get(accountId));
//  }
// }
