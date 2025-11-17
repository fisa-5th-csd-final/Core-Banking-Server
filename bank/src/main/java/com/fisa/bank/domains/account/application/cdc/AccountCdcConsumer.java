// package com.fisa.bank.account.application.cdc;
//
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fisa.bank.common.cdc.event.CdcEvent;
// import com.fisa.bank.common.cdc.event.CdcEventConsumer;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Component;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// @ConditionalOnProperty(prefix = "cdc", name = "enabled", havingValue = "true", matchIfMissing =
// true)
//// CDC 이벤트를 소비하는 소비자
// public class AccountCdcConsumer implements CdcEventConsumer {
//
//  private static final String TABLE_NAME = "account";
//
//  private final ObjectMapper objectMapper;
//  private final AccountCdcProjectionService projectionService;
//
//  @Override
//  public boolean supports(CdcEvent event) {
//    return TABLE_NAME.equalsIgnoreCase(event.table());
//  }
//
//  @Override
//  public void handle(CdcEvent event) {
//    JsonNode snapshot = resolveSnapshot(event);
//    if (snapshot == null) {
//      log.trace("스냅샷이 없어 account CDC 이벤트를 건너뜁니다 (op={})", event.operation());
//      return;
//    }
//    try {
//      AccountCdcPayload payload = objectMapper.treeToValue(snapshot, AccountCdcPayload.class);
//      if ("d".equalsIgnoreCase(event.operation())) {
//        projectionService.remove(payload.accountId(), event.sourceTimestamp());
//      } else {
//        projectionService.upsert(payload, event.sourceTimestamp());
//      }
//    } catch (JsonProcessingException ex) {
//      log.error("Account CDC 페이로드 역직렬화에 실패했습니다", ex);
//    }
//  }
//
//  private JsonNode resolveSnapshot(CdcEvent event) {
//    return switch (event.operation()) {
//      case "c", "u", "r" -> event.after();
//      case "d" -> event.before();
//      default -> null;
//    };
//  }
// }
