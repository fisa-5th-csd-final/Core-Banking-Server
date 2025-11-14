package com.fisa.bank.common.cdc.config;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "cdc")
public class CdcProperties {

  private final boolean enabled;
  private final Kafka kafka;
  private final Debezium debezium;

  @ConstructorBinding
  public CdcProperties(Boolean enabled, Kafka kafka, Debezium debezium) {
    this.enabled = enabled == null || enabled;
    this.kafka = Objects.requireNonNull(kafka, "cdc.kafka 설정이 필요합니다");
    this.debezium = debezium == null ? new Debezium(Collections.emptyMap()) : debezium;
  }

  public boolean consumerAutoStart() {
    return kafka.consumerAutoStart();
  }

  public record Kafka(
      String bootstrapServers, String topic, String consumerGroupId, Boolean consumerAutoStart) {

    public Kafka {
      Objects.requireNonNull(bootstrapServers, "cdc.kafka.bootstrap-servers 값을 입력하세요");
      Objects.requireNonNull(topic, "cdc.kafka.topic 값을 입력하세요");
      Objects.requireNonNull(consumerGroupId, "cdc.kafka.consumer-group-id 값을 입력하세요");
      consumerAutoStart = consumerAutoStart == null || consumerAutoStart;
    }
  }

  public record Debezium(Map<String, String> properties) {

    public Debezium {
      properties = properties == null ? Collections.emptyMap() : Map.copyOf(properties);
        System.out.println(properties);
    }
  }
}
