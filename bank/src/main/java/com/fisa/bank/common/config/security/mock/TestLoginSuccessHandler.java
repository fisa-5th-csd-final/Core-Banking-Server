package com.fisa.bank.common.config.security.mock;

import static com.fisa.bank.common.config.security.jwt.JwtConst.ACCESS_TOKEN;
import static com.fisa.bank.common.config.security.jwt.JwtConst.REFRESH_TOKEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.bank.common.config.security.jwt.UserJwtGenerator;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/** 로그인 성공 핸들러 스프링 시큐리티에 의해, 사용자 인증이 성공하면 Authentication 객체를 Jwt 토큰으로 인코딩하여 ResponseBody에 담는다. */
@Slf4j
@RequiredArgsConstructor
public class TestLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UserJwtGenerator jwtGenerator;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      User user = (User) authentication.getPrincipal();

      if (Objects.nonNull(user)) {

        Long userId = Long.valueOf(user.getUsername());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String accessToken = jwtGenerator.createAccessToken(userId, authorities).getTokenValue();
        String refreshToken = jwtGenerator.createRefreshToken(userId).getTokenValue();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(createBody(accessToken, refreshToken));
        response.flushBuffer();
        return;
      }
      log.warn("UserDetails : {}", user);
      throw new IllegalStateException("UserDetails should be not null");
    }

    // UserIdAuthentication 이 아니면 예외
    log.warn("Authentication : {}", authentication);
    throw new IllegalStateException("Authentication is not UsernamePasswordAuthentication");
  }

  // AccessToken, RefreshToken 이 담긴 바디를 만드는 과정
  private String createBody(String accessToken, String refreshToken) {
    try {
      return objectMapper.writeValueAsString(
          Map.of(ACCESS_TOKEN, accessToken, REFRESH_TOKEN, refreshToken));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Exception occur in json processing");
    }
  }

}
