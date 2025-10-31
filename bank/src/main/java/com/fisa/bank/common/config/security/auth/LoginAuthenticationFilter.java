package com.fisa.bank.common.config.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

public class LoginAuthenticationFilter extends AuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final AuthenticationConverter authenticationConverter;

  public LoginAuthenticationFilter(
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
      try {
        authentication = authenticationManager.authenticate(authentication);

        if (authentication.isAuthenticated()) {
          super.getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
        } else throw new AuthenticationServiceException("발생하면 안되는 예외");
      } catch (AuthenticationException e) {
        super.getFailureHandler().onAuthenticationFailure(request, response, e);
      }

      return; // 더이상 진행 X
    }
    filterChain.doFilter(request, response);
  }
}
