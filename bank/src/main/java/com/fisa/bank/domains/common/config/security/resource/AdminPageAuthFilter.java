package com.fisa.bank.domains.common.config.security.resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * /admin 페이지(HTML) 접근 시 인증이 없으면 /admin/login 으로 리다이렉트하는 필터.
 * (API는 기존 체인에서 401 처리)
 */
@Slf4j
public class AdminPageAuthFilter extends OncePerRequestFilter {

  private final RequestMatcher matcher;
  private final RequestMatcher loginMatcher;

  public AdminPageAuthFilter(RequestMatcher matcher, RequestMatcher loginMatcher) {
    this.matcher = matcher;
    this.loginMatcher = loginMatcher;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    if (loginMatcher.matches(request)) {
      return true; // 로그인 페이지는 통과
    }
    return !matcher.matches(request);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("Unauthenticated admin page access, redirecting to /admin/login");
      response.setStatus(HttpStatus.FOUND.value());
      response.setHeader("Location", "/admin/login");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
