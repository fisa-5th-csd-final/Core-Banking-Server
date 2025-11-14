package com.fisa.bank.common.cdc.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import lombok.Builder;

@Builder
public record CdcEvent (
  @JsonProperty("database") String database,
  @JsonProperty("table") String table,
  @JsonProperty("operation") String operation,
  @JsonProperty("before") JsonNode before,
  @JsonProperty("after") JsonNode after,
  @JsonProperty("sourceTimestamp") Instant sourceTimestamp
){
}
