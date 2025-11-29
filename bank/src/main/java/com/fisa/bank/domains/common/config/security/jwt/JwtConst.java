package com.fisa.bank.domains.common.config.security.jwt;

public class JwtConst {

  // CLAIM Key
  public static final String CLAIM_USER_ID = "user_id";
  public static final String CLAIM_ROLE = "role";
  public static final String CLAIM_ISSUER = "core-bank";

  // Token name
  public static final String ACCESS_TOKEN = "access_token";
  public static final String REFRESH_TOKEN = "refresh_token";

  // Cookie name (의미가 드러나지 않는 축약 키)
  public static final String COOKIE_ACCESS_TOKEN = "sb_at";
  public static final String COOKIE_REFRESH_TOKEN = "sb_rt";
}
