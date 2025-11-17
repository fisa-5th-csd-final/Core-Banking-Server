package com.fisa.bank.infrastructure.cdc.config;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fisa.bank.infrastructure.cdc.event.CdcEvent;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "cdc",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class CdcKafkaConfig {

  private final CdcProperties properties;

  @Bean
  public Map<String, Object> cdcProducerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().bootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    props.put(JsonSerializer.TYPE_MAPPINGS, "cdcEvent:com.fisa.bank.common.cdc.event.CdcEvent");
    return props;
  }

  @Bean
  public ProducerFactory<String, CdcEvent> cdcProducerFactory() {
    return new DefaultKafkaProducerFactory<>(cdcProducerConfigs());
  }

  @Bean
  public KafkaTemplate<String, CdcEvent> cdcKafkaTemplate() {
    return new KafkaTemplate<>(cdcProducerFactory());
  }

  @Bean
  public Map<String, Object> cdcConsumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafka().bootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getKafka().consumerGroupId());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return props;
  }

  @Bean
  public ConsumerFactory<String, CdcEvent> cdcConsumerFactory() {
    JsonDeserializer<CdcEvent> deserializer = new JsonDeserializer<>(CdcEvent.class, false);
    deserializer.addTrustedPackages("com.fisa.bank.common.cdc.event");
    return new DefaultKafkaConsumerFactory<>(
        cdcConsumerConfigs(), new StringDeserializer(), deserializer);
  }

  @Bean(name = "cdcKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, CdcEvent>
      cdcKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CdcEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cdcConsumerFactory());
    factory.setAutoStartup(properties.consumerAutoStart());
    return factory;
  }
}
