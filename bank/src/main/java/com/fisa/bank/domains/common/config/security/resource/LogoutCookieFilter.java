package com.fisa.bank.domains.common.config.security.resource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fisa.bank.domains.common.presentation.util.CookieUtils;

/**
 * /api/logout (POST) 요청 시 토큰 쿠키를 제거하는 필터.
 */
public class LogoutCookieFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !"/api/logout".equals(request.getRequestURI())
        || !"POST".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    CookieUtils.clearTokenCookies(response);
    response.setStatus(HttpStatus.OK.value());
    response.getWriter().write("logged out");
  }
}
