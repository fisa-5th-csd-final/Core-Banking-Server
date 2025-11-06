package com.fisa.bank.common.config.security.resource;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.fisa.bank.user.persistence.repository.UserAuthRepository;

// OAuth2.0 Authorization Server 를 설정하는 Config
@Profile({"local", "dev", "prod"})
@Configuration
public class AuthorizationConfig {

  // Spring Security 의 AuthenticationManger 등록
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * 사용자로부터 자격 증명 (ID/PW)를 받고 인증을 수행하는 필터
   *
   * <p>UsernamePasswordAuthentication 사용
   */
  @Bean("unAuthenticatedFilter")
  public AuthenticationFilter unAuthenticated(
      @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler successHandler,
      @Qualifier("LoginFailureHandler") AuthenticationFailureHandler failureHandler,
      @Qualifier("AppUnAuthenticationConverter") AuthenticationConverter appUnAuthConverter,
      @Qualifier("UsernamePasswordAuthenticationProvider")
          AuthenticationProvider authenticationProvider) {
    AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
    AuthenticationFilter authenticationFilter =
        new LoginAuthenticationFilter(authenticationManager, appUnAuthConverter);
    RequestMatcher requestMatcher =
        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/login");

    authenticationFilter.setRequestMatcher(requestMatcher);
    authenticationFilter.setSuccessHandler(successHandler);
    authenticationFilter.setFailureHandler(failureHandler);

    return authenticationFilter;
  }

  /**
   * 이미 인증이 완료된 사용자가 Jwt를 보내면 이를 Authentication으로 변환해서 저장하는 필터
   *
   * <p>JwtAuthentication 사용
   */
  @Bean("authenticatedFilter")
  public AuthenticationFilter authenticated(
      @Qualifier("JwtAuthenticationProvider") AuthenticationProvider authenticationProvider,
      @Qualifier("JwtAuthenticationConverter") AuthenticationConverter authenticationConverter) {
    AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
    AuthenticationFilter authenticationFilter =
        new JwtAuthenticationFilter(authenticationManager, authenticationConverter);
    RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher("/**");
    authenticationFilter.setRequestMatcher(requestMatcher);

    return authenticationFilter;
  }

  /** jwt 인증필터 서블릿 필터에서 제외 */
  @Bean
  public FilterRegistrationBean<AuthenticationFilter> jwtFilterRegistrationBean(
      @Qualifier("authenticatedFilter") AuthenticationFilter authenticationFilter) {
    FilterRegistrationBean<AuthenticationFilter> registrationBean =
        new FilterRegistrationBean<>(authenticationFilter);
    registrationBean.setEnabled(false); // 서블릿 필터에서 제거
    return registrationBean;
  }

  @Bean("UserDetailsService")
  public UserDetailsService userDetailsService(UserAuthRepository userAuthRepository) {
    return new CustomUserDetailsService(userAuthRepository);
  }

  @Bean("BcryptPasswordEncoder")
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean("JwtAuthenticationConverter")
  public AuthenticationConverter authenticationConverter() {
    return new JwtAuthenticationConverter();
  }

  @Bean("LoginFailureHandler")
  public AuthenticationFailureHandler loginFailureHandler(ObjectMapper om) {
    return new LoginFailureHandler(om);
  }

  @Bean("LoginSuccessHandler")
  public AuthenticationSuccessHandler loginSuccessHandler(
      ObjectMapper objectMapper,
      UserAuthRepository userAuthRepository,
      UserJwtGenerator jwtGenerator) {
    return new LoginSuccessHandler(jwtGenerator, objectMapper, userAuthRepository);
  }

  @Bean("UsernamePasswordAuthenticationProvider")
  public AuthenticationProvider usernamePasswordAuthenticationProvider(
      @Qualifier("BcryptPasswordEncoder") PasswordEncoder passwordEncoder,
      @Qualifier("UserDetailsService") UserDetailsService userDetailsService) {
    return new UsernamePasswordAuthenticationProvider(passwordEncoder, userDetailsService);
  }

  @Bean("JwtAuthenticationProvider")
  public AuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
    return new JwtAuthenticationProvider(jwtDecoder);
  }

  /** 로그인 전용 필터 서블릿 필터에서 제외 */
  @Bean
  public FilterRegistrationBean<AuthenticationFilter> loginFilterRegistrationBean(
      @Qualifier("unAuthenticatedFilter") AuthenticationFilter authenticationFilter) {
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
