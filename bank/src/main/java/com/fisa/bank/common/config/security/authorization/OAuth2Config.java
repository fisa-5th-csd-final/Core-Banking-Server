package com.fisa.bank.common.config.security.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.converter.OidcClientRegistrationRegisteredClientConverter;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
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

    @Bean
    OAuth2TokenGenerator<Jwt> jwtGenerator(JwtEncoder jwtEncoder,
                                           OAuth2TokenCustomizer<JwtEncodingContext> customizer) {
        JwtGenerator gen = new JwtGenerator(jwtEncoder);
        gen.setJwtCustomizer(customizer);
        return gen;
    }

  /** OAuth2Token 생성기 */
  @Bean
  public OAuth2TokenGenerator<?> tokenGenerator(OAuth2TokenGenerator<Jwt> jwtGenerator) {
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  /** require_proof_key false 설정 */
  @Bean("OidcClientRegistrationConverter")
  public Converter<OidcClientRegistration, RegisteredClient> clientConverter(){
      OidcClientRegistrationRegisteredClientConverter delegate = new OidcClientRegistrationRegisteredClientConverter();

      return (source) -> {
          RegisteredClient base = delegate.convert(source);
          // 클라이언트가 보낸 require_proof_key(불리언)를 그대로 존중
          Object raw = source.getClaims().get("require_proof_key");

          boolean reqPkce = (raw instanceof Boolean b) ? b : false; // 값이 없으면 기본 false(서버 앱에 유리)

          ClientSettings newSettings = ClientSettings
                  .withSettings(base.getClientSettings().getSettings())
                  .requireProofKey(reqPkce)
                  .build();

          return RegisteredClient.from(base).clientSettings(newSettings).build();
      };
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

  // 서블릿 필터에만 등록하기 위함
  @Bean
    public FilterRegistrationBean<DynamicClientRegisterFilter> dcrFilterRegistrationBean(DynamicClientRegisterFilter filter){
      FilterRegistrationBean<DynamicClientRegisterFilter> registrationBean = new FilterRegistrationBean<>(filter);

      registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
      return registrationBean;
  }
}
