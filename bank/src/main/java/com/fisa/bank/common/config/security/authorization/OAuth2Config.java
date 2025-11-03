package com.fisa.bank.common.config.security.authorization;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

  // OAuth 2.0 클라이언트 저장소 등록
  // 인메모리, JDBC 선택 가능
  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbc) {
    return new JdbcRegisteredClientRepository(jdbc);
  }

  @Bean("OAuth2ClientAuthenticationProvider")
  public AuthenticationProvider clientSecret(RegisteredClientRepository registeredClientRepository,
                                             OAuth2AuthorizationService oAuth2AuthorizationService){
      return new ClientSecretAuthenticationProvider(registeredClientRepository, oAuth2AuthorizationService);
  }
  /** OAuth2Token 생성기 */
  @Bean
  public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder) {
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  // 인증/인가 동의 저장소
  @Bean
  OAuth2AuthorizationService authorizationService(
      JdbcTemplate jdbc, RegisteredClientRepository repo) {
    return new JdbcOAuth2AuthorizationService(jdbc, repo);
  }

  @Bean
  OAuth2AuthorizationConsentService authorizationConsentService(
      JdbcTemplate jdbc, RegisteredClientRepository repo) {
    return new JdbcOAuth2AuthorizationConsentService(jdbc, repo);
  }
}
