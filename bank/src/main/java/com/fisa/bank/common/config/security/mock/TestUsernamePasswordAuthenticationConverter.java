package com.fisa.bank.common.config.security.mock;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

/** 사용자가 요청한 HTTP Request에서 자격 증명(ID/PW)을 꺼내서 Authentication으로 만드는 역할 */
@Component("TestUsernamePasswordConverter")
public class TestUsernamePasswordAuthenticationConverter implements AuthenticationConverter {

  @Override
  public Authentication convert(HttpServletRequest request) {
      try{
          String userId = request.getRequestURI().replace("/api/login/", "");

          return new UsernamePasswordAuthenticationToken(userId, "{noop}password");
    } catch (Exception e) {
        throw new AuthenticationServiceException("Failed to create UsernamePasswordAuthentication", e);
    }
  }
}
