package com.fisa.bank.common.config.security.jwt;

import lombok.Getter;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private final AccessToken accessToken;
  private final RefreshToken refreshToken;

  @ConstructorBinding
  public JwtProperties(AccessToken accessToken, RefreshToken refreshToken, String secretKey) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public record AccessToken(Duration expiry) {}

  public record RefreshToken(Duration expiry) {}

  public Duration getAccessTokenExpiration() {
    return this.accessToken.expiry;
  }

  public Duration getRefreshTokenExpiration() {
    return this.refreshToken.expiry;
  }
}
