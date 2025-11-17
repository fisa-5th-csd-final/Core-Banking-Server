// package com.fisa.bank.account.application.cdc;
//
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
//
//// CDC 스냅샷 페이로드
// @JsonIgnoreProperties(ignoreUnknown = true)
// public record AccountCdcPayload(
//    @JsonProperty("account_id") Long accountId,
//    @JsonProperty("account_number") String accountNumber,
//    @JsonProperty("user_id") Long userId,
//    BigDecimal balance,
//    @JsonProperty("bank_code") String bankCode,
//    @JsonProperty("created_at") LocalDateTime createdAt,
//    @JsonProperty("updated_at") LocalDateTime updatedAt,
//    @JsonProperty("deleted_at") LocalDateTime deletedAt) {}
