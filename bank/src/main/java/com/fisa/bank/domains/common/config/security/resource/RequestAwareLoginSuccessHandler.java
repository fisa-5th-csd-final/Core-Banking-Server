package com.fisa.bank.domains.common.config.security.resource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.fisa.bank.domains.common.config.security.jwt.UserJwtGenerator;
import com.fisa.bank.domains.common.presentation.util.CookieUtils;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;

/**
 * 폼 로그인 성공 시 JWT 쿠키를 심은 뒤, SavedRequest로 리다이렉트한다.
 */
  @Slf4j
  @RequiredArgsConstructor
  public class RequestAwareLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private final UserJwtGenerator jwtGenerator;
  private final UserAuthRepository userAuthRepository;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    if (authentication instanceof UsernamePasswordAuthenticationToken token) {
      User user = (User) token.getPrincipal();

      if (Objects.nonNull(user)) {
        Long userId = getUserId(user.getUsername());
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String accessToken = jwtGenerator.createAccessToken(userId, authorities).getTokenValue();
        String refreshToken = jwtGenerator.createRefreshToken(userId).getTokenValue();

        CookieUtils.addTokenCookies(response, accessToken, refreshToken);
      } else {
        log.warn("UserDetails is null");
        throw new IllegalStateException("UserDetails should be not null");
      }
    }

    // SavedRequest(예: OAuth2 redirect)로 이동
    super.onAuthenticationSuccess(request, response, authentication);
  }

  private Long getUserId(String loginId) {
    return userAuthRepository
        .findUserIdByLoginId(loginId)
        .orElseThrow(
            () -> new UsernameNotFoundException("username %s not found".formatted(loginId)))
        .getValue();
  }
}
