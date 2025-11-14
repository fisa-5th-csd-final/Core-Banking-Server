# CDC + Kafka 빠른 가이드

이제 이 프로젝트에는 MySQL(binlog) → Debezium → Kafka 로 이어지는 최소한의 CDC 파이프라인이 포함되어 있습니다. 또한 주석 처리한 소비자/리스너를 활성화하여, 추후 Kafka 토픽을 구독해 후속 처리를 수행할 수 있습니다.

## 구성 요소
- `application-cdc.yml`: CDC/Kafka/Debezium 기본값을 정의. 모든 항목은 환경변수로 덮어쓸 수 있습니다.
- `CdcProperties`: 위 설정을 바인딩하며 `CDC_ENABLED=false` 로 전체 CDC 기능을 끌 수 있습니다.
- `DebeziumEngineConfiguration`: 임베디드 Debezium 엔진을 기동해 설정된 MySQL 테이블을 tail 하고, 발생한 변경 이벤트를 `CdcEventPublisher`에 전달합니다.
- `CdcEventPublisher`: Debezium payload를 `CdcEvent` DTO로 정규화 후 `cdc.kafka.topic` 에 설정된 Kafka 토픽으로 발행합니다.
- *(옵션)* `CdcEventListener`, `AccountCdcConsumer`: Kafka 이벤트를 애플리케이션 내부에서 소비하는 샘플 코드입니다. 현재 서버에서는 주석 처리되어 있으며, 다른 서버로부터 메세지를 받을 경우에 주석 해제하여 사용할 수 있습니다.

## 필요한 인프라
1. **MySQL**: ROW 모드 binlog 활성화 + `REPLICATION SLAVE`, `REPLICATION CLIENT` 권한을 가진 사용자.
2. **Kafka**: 본 서비스에서 접근 가능한 브로커(필요 시 Schema Registry 포함).
3. Debezium offset/schema history 파일을 저장할 파일 시스템 경로(기본값 `build/debezium`).

## 주요 환경변수 예시

Spring Boot 실행 전 아래와 같이 설정합니다(로컬 개발 기준):

```bash
export PROFILE=local
export DATABASE_URL=jdbc:mysql://localhost:3306/core_bank?useSSL=false&serverTimezone=Asia/Seoul
export DATABASE_USERNAME=bank_app
export DATABASE_PASSWORD=bank_app_pw

export CDC_KAFKA_BOOTSTRAP_SERVERS=localhost:29092 # 컨테이너 내부에서는 kafka:9092
export CDC_KAFKA_TOPIC=bank.cdc.changelog

export CDC_DEBEZIUM_DB_HOST=localhost           # 도커 컨테이너 안에서는 mysql 서비스명 사용
export CDC_DEBEZIUM_DB_PORT=3306
export CDC_DEBEZIUM_DATABASE_INCLUDE=core_bank
export CDC_DEBEZIUM_TABLE_INCLUDE=core_bank.account
export CDC_DEBEZIUM_SNAPSHOT_MODE=initial
export CDC_DEBEZIUM_TIMEZONE=Asia/Seoul
```

그 외 커넥터 옵션은 `CDC_DEBEZIUM_*` 환경변수로 1:1 매핑되어 있으니 필요 시 추가로 지정하면 됩니다.

## 로컬 실행 체크리스트
1. Kafka + ZooKeeper 기동 (예: `docker compose up kafka zookeeper`). 호스트에서 접근 시 `localhost:29092`.
2. MySQL binlog 활성화 및 대상 테이블(`core_bank.account`)에 PK 존재 여부 확인.
3. Spring Boot 실행: `./gradlew :bank:bootRun`.
4. Kafka 토픽에서 이벤트 확인:
   ```bash
   kafka-console-consumer --bootstrap-server localhost:29092 --topic bank.cdc.changelog --from-beginning
   ```

### Account 도메인 CDC 처리 예시
- Debezium이 내려주는 `before/after` 스냅샷을 `AccountCdcPayload`로 역직렬화한 뒤, `c`,`u`,`r` 이벤트는 upsert, `d` 이벤트는 remove 로직에 매핑할 수 있습니다.
- 실서비스에서는 해당 패턴을 별도 Consumer 애플리케이션, Redis/ES 업데이트 작업 등에 맞게 확장하면 됩니다.
