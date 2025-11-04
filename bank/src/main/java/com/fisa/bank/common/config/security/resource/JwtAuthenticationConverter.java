package com.fisa.bank.common.config.security.resource;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

/** 클라이언트가 보낸 Jwt 토큰을 Authentication 으로 변환해주는 역할 */
@Component("AppAuthenticationConverter")
public class JwtAuthenticationConverter implements AuthenticationConverter {

  @Override
  public Authentication convert(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    String token = header.substring(7);
    if (token.isBlank()) {
      return null; // null 반환, 필터 체인의 AuthorizationFilter에서 최종 인증/인가 여부를 판별한다.
    }
    return new JwtAuthentication(token);
  }
}
