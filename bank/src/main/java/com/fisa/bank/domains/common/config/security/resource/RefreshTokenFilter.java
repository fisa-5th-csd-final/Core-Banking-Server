package com.fisa.bank.domains.common.config.security.resource;

import static com.fisa.bank.domains.common.config.security.jwt.JwtConst.CLAIM_USER_ID;
import static com.fisa.bank.domains.common.config.security.jwt.JwtConst.COOKIE_REFRESH_TOKEN;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.bank.domains.common.config.security.jwt.UserJwtGenerator;
import com.fisa.bank.domains.common.presentation.util.CookieUtils;
import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;
import com.fisa.bank.domains.user.persistence.enums.UserRole;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;

/**
 * Refresh Token 재발급 필터. /api/token/refresh (POST) 에서 Authorization Bearer 또는 refresh_token 쿠키로 토큰을
 * 받아 검증 후 새 토큰을 반환한다.
 */
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;
  private final UserJwtGenerator userJwtGenerator;
  private final UserAuthRepository userAuthRepository;
  private final ObjectMapper objectMapper;
  private final RequestMatcher matcher;

  public RefreshTokenFilter(
      JwtDecoder jwtDecoder,
      UserJwtGenerator userJwtGenerator,
      UserAuthRepository userAuthRepository,
      ObjectMapper objectMapper,
      RequestMatcher matcher) {
    this.jwtDecoder = jwtDecoder;
    this.userJwtGenerator = userJwtGenerator;
    this.userAuthRepository = userAuthRepository;
    this.objectMapper = objectMapper;
    this.matcher = matcher;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !matcher.matches(request);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = resolveToken(request, HttpHeaders.AUTHORIZATION, COOKIE_REFRESH_TOKEN);
    if (token == null || token.isBlank()) {
      writeError(response, HttpStatus.UNAUTHORIZED, "Refresh token not found");
      return;
    }

    try {
      Jwt jwt = jwtDecoder.decode(token);
      Long userId = jwt.getClaim(CLAIM_USER_ID);
      if (userId == null) {
        writeError(response, HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        return;
      }

      Optional<UserAuth> userAuthOpt = userAuthRepository.findByUserId(UserId.of(userId));
      if (userAuthOpt.isEmpty()) {
        writeError(response, HttpStatus.UNAUTHORIZED, "User not found");
        return;
      }
      UserAuth userAuth = userAuthOpt.get();
      Collection<? extends GrantedAuthority> authorities = toAuthorities(userAuth.getRole());

      String newAccess = userJwtGenerator.createAccessToken(userId, authorities).getTokenValue();
      String newRefresh = userJwtGenerator.createRefreshToken(userId).getTokenValue();
      CookieUtils.addTokenCookies(response, newAccess, newRefresh);

      response.setStatus(HttpStatus.OK.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response
          .getWriter()
          .write(
              objectMapper.writeValueAsString(
                  Map.of("access_token", newAccess, "refresh_token", newRefresh)));
    } catch (Exception e) {
      writeError(response, HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }
  }

  private Collection<? extends GrantedAuthority> toAuthorities(UserRole role) {
    if (role == null) return List.of();
    return switch (role) {
      case ADMIN -> List.of(
          new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
      case USER -> List.of(new SimpleGrantedAuthority("ROLE_USER"));
    };
  }

  private String resolveToken(HttpServletRequest request, String headerName, String cookieName) {
    String header = request.getHeader(headerName);
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie != null && cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void writeError(HttpServletResponse response, HttpStatus status, String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
    response.getWriter().write(message);
  }
}
