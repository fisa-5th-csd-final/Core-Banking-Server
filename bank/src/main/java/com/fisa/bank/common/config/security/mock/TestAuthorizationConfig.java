package com.fisa.bank.common.config.security.mock;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.bank.common.config.security.jwt.UserJwtGenerator;
import com.fisa.bank.common.config.security.resource.JwtAuthenticationConverter;
import com.fisa.bank.common.config.security.resource.JwtAuthenticationProvider;
import com.fisa.bank.common.config.security.resource.LoginFailureHandler;
import com.fisa.bank.common.config.security.resource.UnknownEndPointFilter;

@Profile({"test"})
@Configuration
@RequiredArgsConstructor
public class TestAuthorizationConfig {

  // Spring Security 의 AuthenticationManger 등록
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean("TestLoginAuthenticationFilter")
  public AuthenticationFilter testLoginFilter(
      @Qualifier("TestUsernamePasswordAuthenticationProvider")
          AuthenticationProvider authenticationProvider,
      @Qualifier("TestLoginFailureHandler") AuthenticationFailureHandler failureHandler,
      @Qualifier("TestLoginSuccessHandler") AuthenticationSuccessHandler successHandler,
      @Qualifier("TestUsernamePasswordConverter") AuthenticationConverter authenticationConverter) {
    AuthenticationFilter authenticationFilter =
        new TestLoginAuthenticationFilter(
            new ProviderManager(authenticationProvider), authenticationConverter);

    RequestMatcher requestMatcher =
        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/login/{userId}");

    authenticationFilter.setRequestMatcher(requestMatcher);
    authenticationFilter.setFailureHandler(failureHandler);
    authenticationFilter.setSuccessHandler(successHandler);

    return authenticationFilter;
  }

  @Bean("TestJwtAuthenticationFilter")
  public AuthenticationFilter testJwtFilter(
      @Qualifier("TestJwtAuthenticationProvider") AuthenticationProvider authenticationProvider,
      @Qualifier("TestAuthenticationConverter") AuthenticationConverter authenticationConverter) {
    AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
    AuthenticationFilter authenticationFilter =
        new TestJwtAuthenticationFilter(authenticationManager, authenticationConverter);

    RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher("/**");

    authenticationFilter.setRequestMatcher(requestMatcher);

    return authenticationFilter;
  }

  @Bean("TestJwtAuthenticationProvider")
  public AuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
    return new JwtAuthenticationProvider(jwtDecoder);
  }

  @Bean("TestUsernamePasswordAuthenticationProvider")
  public AuthenticationProvider usernameProvider() {
    return new TestUsernamePasswordAuthenticationProvider();
  }

  @Bean("TestLoginFailureHandler")
  public AuthenticationFailureHandler loginFailureHandler(ObjectMapper om) {
    return new LoginFailureHandler(om);
  }

  @Bean("TestLoginSuccessHandler")
  public AuthenticationSuccessHandler loginSuccessHandler(
      ObjectMapper objectMapper, UserJwtGenerator jwtGenerator) {
    return new TestLoginSuccessHandler(jwtGenerator, objectMapper);
  }

  @Bean("TestAuthenticationConverter")
  public AuthenticationConverter authenticationConverter() {
    return new JwtAuthenticationConverter();
  }

  @Bean("TestPasswordEncoder")
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean("TestUserDetailsService")
  public UserDetailsService userDetailsService() {
    return new TestUserDetailsService();
  }

  /** 로그인 전용 필터 서블릿 필터에서 제외 */
  @Bean
  public FilterRegistrationBean<AuthenticationFilter> loginFilterRegistrationBean(
      @Qualifier("TestLoginAuthenticationFilter") AuthenticationFilter authenticationFilter) {
    FilterRegistrationBean<AuthenticationFilter> registrationBean =
        new FilterRegistrationBean<>(authenticationFilter);
    registrationBean.setEnabled(false); // 서블릿 필터에서 제거
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<UnknownEndPointFilter> unknownFilterRegistrationBean(
      UnknownEndPointFilter unknownEndPointFilter) {
    FilterRegistrationBean<UnknownEndPointFilter> registrationBean =
        new FilterRegistrationBean<>(unknownEndPointFilter);
    registrationBean.setEnabled(false); // 서블릿 필터에서 제거
    return registrationBean;
  }
}
