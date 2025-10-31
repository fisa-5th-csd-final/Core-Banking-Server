package com.fisa.bank.common.config.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Reader;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 사용자가 요청한 HTTP Request에서 자격 증명(ID/PW)을 꺼내서 Authentication으로 만드는 역할 */
@Component("AppUnAuthenticationConverter")
@RequiredArgsConstructor
public class UserIdAuthenticationConverter implements AuthenticationConverter {

  // RequestBody -> JSON 변환
  private final ObjectMapper objectMapper;

  @Override
  public Authentication convert(HttpServletRequest request) {

    try (Reader reader = request.getReader()) {
      JsonNode jsonNode = objectMapper.readTree(reader);

      String loginId = jsonNode.get("loginId").asText(null);
      String password = jsonNode.get("password").asText(null);

      Assert.notNull(loginId, "loginId should be not null");
      Assert.notNull(password, "password should be not null");

      return new UsernamePasswordAuthenticationToken(loginId, password);
    } catch (IOException e) {
      throw new AuthenticationServiceException("Failed to parse authentication request body", e);
    }
  }
}
