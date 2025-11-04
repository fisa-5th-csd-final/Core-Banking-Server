package com.fisa.bank.common.config.security.resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

/** 사용자가 Authorization에 보낸 토큰을 추출해서 Authentication 객체로 변환하는 역할 */
@Slf4j
public class JwtAuthenticationFilter extends AuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final AuthenticationConverter authenticationConverter;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      AuthenticationConverter authenticationConverter) {
    super(authenticationManager, authenticationConverter);
    this.authenticationManager = authenticationManager;
    this.authenticationConverter = authenticationConverter;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (super.getRequestMatcher().matches(request)) {
      // Authentication 변환
      Authentication authentication = authenticationConverter.convert(request);

      if (Objects.nonNull(authentication)) {
        try {
          // 인증 진행
          authentication = authenticationManager.authenticate(authentication);
          // 컨텍스트 저장
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
          log.warn("인증 예외 발생 : {}", e.getMessage());
          SecurityContextHolder.clearContext();
        } catch (Exception e) {
          log.warn("예상하지 못한 예외 발생 : {}", e.getMessage());
          SecurityContextHolder.clearContext();
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
