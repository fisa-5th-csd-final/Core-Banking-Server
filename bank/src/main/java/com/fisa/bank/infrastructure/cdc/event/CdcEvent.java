package com.fisa.bank.infrastructure.cdc.event;

import lombok.Builder;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@Builder
public record CdcEvent(
    @JsonProperty("database") String database,
    @JsonProperty("table") String table,
    @JsonProperty("operation") String operation,
    @JsonProperty("before") JsonNode before,
    @JsonProperty("after") JsonNode after,
    @JsonProperty("sourceTimestamp") Instant sourceTimestamp) {}
