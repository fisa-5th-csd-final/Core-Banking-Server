package com.fisa.bank.common.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile({"test"})
@Configuration
@RequiredArgsConstructor
public class TestSecurityFilterChainConfig {

  /** 테스트 환경에서 임시 토큰을 발급 받기 위한 시큐리티 필터 체인 무조건 로그인에 성공하며, 토큰이 발급된다. */
  @Bean
  @Order(1)
  public SecurityFilterChain login(
      @Qualifier("TestLoginAuthenticationFilter") AuthenticationFilter loginFilter,
      HttpSecurity http)
      throws Exception {
    commonConfigurations(http);

    http.securityMatchers(
            matcher -> {
              matcher.requestMatchers(HttpMethod.POST, "/api/login/{userId}");
            })
        .authorizeHttpRequests(request -> request.anyRequest().permitAll());
    http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /** 테스트 환경에서 AccessToken을 받는 엔드포인트 액세스 토큰이 있든 없든 통과한다. */
  @Bean
  @Order(2)
  public SecurityFilterChain api(
      @Qualifier("TestJwtAuthenticationFilter") AuthenticationFilter authenticationFilter,
      HttpSecurity http)
      throws Exception {
    commonConfigurations(http);

    http.securityMatchers(matcher -> matcher.requestMatchers("/api/**"))
        .authorizeHttpRequests(request -> request.anyRequest().authenticated());

    http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  public void commonConfigurations(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
    http.sessionManagement(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.logout(AbstractHttpConfigurer::disable);
  }
}
