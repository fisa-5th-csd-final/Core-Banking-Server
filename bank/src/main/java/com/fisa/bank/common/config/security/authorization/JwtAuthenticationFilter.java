package com.fisa.bank.common.config.security.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

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
      // 인증 진행
      authentication = authenticationManager.authenticate(authentication);

      // 컨텍스트 저장
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }
}
