package com.fisa.bank.common.config.security.resource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 로그인 실패 시 핸들러 401 unAuthorized 응답과 합께 에러 응답을 바디로 담아서 보낸다. */
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(createFailureBody());
    response.flushBuffer();
  }

  private String createFailureBody() {
    try {
      return objectMapper.writeValueAsString(
          Map.of("error", "unauthorized", "message", "인증 정보가 올바르지 않습니다."));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Exception occur in json processing");
    }
  }
}
