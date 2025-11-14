//package com.fisa.bank.common.cdc.event;
//
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Component;
//
//@Slf4j
////@Component
//@RequiredArgsConstructor
//@ConditionalOnProperty(prefix = "cdc", name = "enabled", havingValue = "true", matchIfMissing = true)
//public class CdcEventListener {
//
//  private final List<CdcEventConsumer> consumers;
//
//  @KafkaListener(
//      topics = "${cdc.kafka.topic}",
//      groupId = "${cdc.kafka.consumer-group-id}",
//      containerFactory = "cdcKafkaListenerContainerFactory",
//      autoStartup = "${cdc.kafka.consumer-auto-start:true}")
//  public void onMessage(
//      CdcEvent event, @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
//    boolean handled = false;
//    for (CdcEventConsumer consumer : consumers) {
//      if (consumer.supports(event)) {
//        consumer.handle(event);
//        handled = true;
//      }
//    }
//    if (!handled) {
//      log.trace(
//          "처리 대상 Consumer가 없어 CDC 이벤트를 건너뜁니다 (key={} table={} op={})",
//          key,
//          event.table(),
//          event.operation());
//    }
//  }
//}
