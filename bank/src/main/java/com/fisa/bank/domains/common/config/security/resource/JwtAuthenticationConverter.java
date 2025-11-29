package com.fisa.bank.domains.common.config.security.resource;

import static com.fisa.bank.domains.common.config.security.jwt.JwtConst.COOKIE_ACCESS_TOKEN;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

import jakarta.servlet.http.Cookie;

/** 클라이언트가 보낸 Jwt 토큰을 Authentication 으로 변환해주는 역할 */
public class JwtAuthenticationConverter implements AuthenticationConverter {

  @Override
  public Authentication convert(HttpServletRequest request) {
    String token = resolveFromHeader(request);
    if (token == null) {
      token = resolveFromCookie(request, COOKIE_ACCESS_TOKEN);
    }
    if (token == null || token.isBlank()) {
      return null; // null 반환, 필터 체인의 AuthorizationFilter에서 최종 인증/인가 여부를 판별한다.
    }
    return new JwtAuthentication(token);
  }

  private String resolveFromHeader(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    return header.substring(7);
  }

  private String resolveFromCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;
    for (Cookie cookie : cookies) {
      if (cookie != null && name.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
