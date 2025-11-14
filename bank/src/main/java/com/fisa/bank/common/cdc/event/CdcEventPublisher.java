package com.fisa.bank.common.cdc.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.bank.common.cdc.config.CdcProperties;
import io.debezium.engine.ChangeEvent;
import java.io.IOException;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cdc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CdcEventPublisher {

  private final KafkaTemplate<String, CdcEvent> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final CdcProperties properties;

  public void publish(ChangeEvent<String, String> changeEvent) {
    if (changeEvent == null || changeEvent.value() == null) {
      return;
    }
    try {
      JsonNode payload = extractPayload(changeEvent.value());
      if (payload == null) {
        return;
      }
      JsonNode source = payload.path("source");
      String table = source.path("table").asText("");
      String database = source.path("db").asText("");
      Instant ts = Instant.ofEpochMilli(source.path("ts_ms").asLong(0));
      CdcEvent event =
          CdcEvent.builder()
              .database(database)
              .table(table)
              .operation(payload.path("op").asText(""))
              .before(payload.get("before"))
              .after(payload.get("after"))
              .sourceTimestamp(ts)
              .build();
      String key = changeEvent.key() == null ? table : changeEvent.key();
      kafkaTemplate.send(properties.getKafka().topic(), key, event);
      log.debug("CDC 이벤트를 발행했습니다 table={} op={}", table, event.operation());
    } catch (IOException ex) {
      log.error("CDC 이벤트 발행 중 오류가 발생했습니다", ex);
    }
  }

  private JsonNode extractPayload(String rawValue) throws IOException {
    JsonNode valueNode = objectMapper.readTree(rawValue);
    JsonNode payload = valueNode.get("payload");
    if (payload == null || payload.isNull()) {
      log.trace("payload 가 없어 CDC 메시지를 건너뜁니다");
      return null;
    }
    JsonNode opNode = payload.get("op");
    if (opNode == null || opNode.isNull()) {
      return null;
    }
    return payload;
  }
}
