package com.fisa.bank.common.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import com.fisa.bank.common.config.security.resource.RequiredAuthenticationEntryPoint;
import com.fisa.bank.common.config.security.resource.UnknownEndPointFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

  private final RequiredAuthenticationEntryPoint requiredAuthenticationEntryPoint;

  @Bean
  @Order(1)
  // Authorization Server 필터 체인 설정
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http,
      OAuth2TokenGenerator<?> tokenGenerator,
      JwtDecoder jwtDecoder,
      @Qualifier("OidcClientRegistrationConverter")
          Converter<OidcClientRegistration, RegisteredClient> converter)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();

    //    commonConfiguration(http); // 공통 설정
    http.formLogin(Customizer.withDefaults());
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    http.csrf(AbstractHttpConfigurer::disable);
    // SAS 엔드포인트만 매칭

    http.securityMatcher(authorizationServer.getEndpointsMatcher())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/oauth2/authorize").authenticated().anyRequest().permitAll());

    // SAS 기능 활성화(OIDC 포함)
    http.with(
            authorizationServer,
            as ->
                as.tokenGenerator(tokenGenerator)
                    .oidc(
                        oidc ->
                            oidc.clientRegistrationEndpoint(
                                    c ->
                                        c.authenticationProviders(
                                            providers -> {
                                              for (var p : providers) {
                                                if (p
                                                    instanceof
                                                    OidcClientRegistrationAuthenticationProvider
                                                    provider) {
                                                  provider.setRegisteredClientConverter(converter);
                                                }
                                              }
                                            }))
                                .userInfoEndpoint(Customizer.withDefaults())))
        // 인증 안 된 HTML 요청은 /login으로
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  @Order(2)
  // [일반 사용자용] 인증이 필요하지 않은 엔드포인트
  public SecurityFilterChain unAuthenticated(
      HttpSecurity http, @Qualifier("unAuthenticatedFilter") AuthenticationFilter loginFilter)
      throws Exception {
    commonConfiguration(http);

    http.securityMatchers(
            matcher ->
                matcher
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/loans/products",
                        "/api/loans/{loanProductId}",
                        "/api/interests/{loanProductId}",
                        "/swagger-ui/index.html", // TODO: Swagger 전용 필터체인으로 분리
                        "/v3/api-docs", // TODO: Swagger 전용 필터체인으로 분리
                        "/swagger-resources/**" // TODO: Swagger 전용 필터체인으로 분리
                        )
                    .requestMatchers(HttpMethod.POST, "/api/loans", "/api/login", "/api/users")
                    .requestMatchers(HttpMethod.DELETE, "/api/loans/products/{loanProductId}"))
        .authorizeHttpRequests(request -> request.anyRequest().permitAll());

    http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class); // login 전용 필터
    http.oauth2ResourceServer(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  @Order(3)
  // [일반 사용자용] 인증이 필요한 엔드포인트 시큐리티 필터체인
  public SecurityFilterChain authenticated(
      HttpSecurity http,
      @Qualifier("authenticatedFilter") AuthenticationFilter authenticationFilter)
      throws Exception {

    commonConfiguration(http);

    http.securityMatchers(
            matcher ->
                matcher
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/loans/{loanProductId}",
                        "/api/loans/{loanLedgerId}/repayment",
                        "/api/accounts",
                        "/api/accounts/{accountNumber}/deposit",
                        "/api/accounts/{accountNumber}/pay",
                        "/api/accounts/{accountNumber}/withdraw",
                        "/api/accounts/transfer")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/users/me",
                        "/api/accounts/{accountNumber}",
                        "/api/accounts",
                        "/api/accounts/{accountNumber}/transactions",
                        "/api/loans",
                        "/api/loans/ledger/{loanLedgerId}",
                        "/api/loans/ledgers/{userId}")
                    .requestMatchers(HttpMethod.DELETE, "/api/accounts/{accountNumber}"))
        .authorizeHttpRequests(request -> request.anyRequest().authenticated());

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    http.exceptionHandling(ex -> ex.authenticationEntryPoint(requiredAuthenticationEntryPoint));
    http.oauth2ResourceServer(AbstractHttpConfigurer::disable);
    return http.build();
  }

  /** 시큐리티 기본 로그인 및 에러 리다이렉트 필터체인 */
  @Bean
  @Order(4)
  public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {

    http.securityMatchers(
            matcher -> matcher.requestMatchers("/login", "/default-ui.css", "/error/**"))
        .authorizeHttpRequests(request -> request.anyRequest().permitAll());

    http.formLogin(Customizer.withDefaults()); // form Login 활성화
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

    return http.build();
  }

  @Bean
  @Order(5)
  public SecurityFilterChain unknownFilterChain(
      HttpSecurity httpSecurity, UnknownEndPointFilter unknownEndPointFilter) throws Exception {
    httpSecurity.securityMatcher("/**");

    commonConfiguration(httpSecurity);
    httpSecurity.logout(AbstractHttpConfigurer::disable);
    // 서버가 처리할 수 있는 엔드포인트인지 확인하는 필터
    httpSecurity.addFilterBefore(unknownEndPointFilter, DisableEncodeUrlFilter.class);
    return httpSecurity.build();
  }

  // FilterChain 공통 설정
  private void commonConfiguration(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
    http.sessionManagement(
        sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 비활성화
  }
}
