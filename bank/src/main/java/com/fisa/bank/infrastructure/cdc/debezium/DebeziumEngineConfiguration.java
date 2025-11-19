package com.fisa.bank.infrastructure.cdc.debezium;

import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.fisa.bank.infrastructure.cdc.config.CdcProperties;
import com.fisa.bank.infrastructure.cdc.event.CdcEventPublisher;

@Slf4j
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "cdc",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class DebeziumEngineConfiguration {

  private final CdcProperties cdcProperties;
  private final CdcEventPublisher eventPublisher;

  @Bean(destroyMethod = "shutdown") // 스프링이 종료될 때 executorService.shutdown() 호출
  public ExecutorService cdcExecutorService() {
    return Executors.newSingleThreadExecutor(
        runnable -> {
          Thread thread = new Thread(runnable);
          thread.setName("cdc-debezium-engine");
          thread.setDaemon(true);
          return thread;
        });
  }

  @Bean
  public Configuration debeziumConfiguration() {
    Configuration.Builder builder = Configuration.create();
    cdcProperties.getDebezium().properties().forEach(builder::with);
    return builder.build();
  }

  @Bean(destroyMethod = "close")
  public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine(
      Configuration debeziumConfiguration) {
    return DebeziumEngine.create(Json.class)
        .using(debeziumConfiguration.asProperties())
        .notifying(eventPublisher::publish)
        .using(
            (success, message, error) -> {
              if (Boolean.TRUE.equals(success)) {
                log.info("Debezium 엔진이 종료되었습니다: {}", message);
              } else if (error != null) {
                log.error("Debezium 엔진이 오류로 중단되었습니다: {}", message, error);
              }
            })
        .build();
  }

  @Bean
  public ApplicationRunner debeziumRunner(
      DebeziumEngine<ChangeEvent<String, String>> debeziumEngine,
      ExecutorService cdcExecutorService) {
    return args -> {
      if (!cdcProperties.isEnabled()) {
        log.info("CDC 기능이 비활성화되어 Debezium 엔진을 기동하지 않습니다");
        return;
      }
      cdcExecutorService.execute(debeziumEngine);
      log.info(
          "Debezium 엔진을 시작했습니다 (topic={} connector={})",
          cdcProperties.getKafka().topic(),
          Objects.requireNonNullElse(
              cdcProperties.getDebezium().properties().get("name"), "bank-connector"));
    };
  }
}
