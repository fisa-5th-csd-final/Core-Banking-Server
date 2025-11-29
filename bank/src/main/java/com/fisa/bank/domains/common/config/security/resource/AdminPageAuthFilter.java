package com.fisa.bank.domains.common.config.security.resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/** /admin 페이지(HTML) 접근 시 인증이 없으면 /admin/login 으로 리다이렉트하는 필터. (API는 기존 체인에서 401 처리) */
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
    return !matcher.matches(request);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isLoginPage = loginMatcher.matches(request);

    // 인증이 안된 경우
    if (authentication == null || !authentication.isAuthenticated()) {
      // 로그인 페이지일 경우
      if (isLoginPage) {
        filterChain.doFilter(request, response); // 필터 통과
        return;
      }
      // 로그인 페이지가 아닌 경우, 로그인 페이지로 리다이렉트
      log.debug("인증되지 않은 관리자 페이지 접근, 리다이렉트 : /admin/login");
      response.sendRedirect("/admin/login");
      return;
    }

    // 인증된 경우
    // 로그인 페이지로 접속하려고 하는 경우
    if (isLoginPage) {
      // 이미 인증된 상태에서 /admin/login 접근 시 메인으로 이동
      response.sendRedirect("/admin");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
