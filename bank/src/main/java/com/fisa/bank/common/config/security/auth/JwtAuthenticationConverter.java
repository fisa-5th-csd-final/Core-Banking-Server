package com.fisa.bank.common.config.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * 클라이언트가 보낸 Jwt 토큰을 Authentication 으로 변환해주는 역할
 * Jwt 토큰이 유효하면, 바로 인증완료를 수행한다.
 */
@Component("AppAuthenticationConverter")
public class JwtAuthenticationConverter implements AuthenticationConverter {


    @Override
    public Authentication convert(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        if(token.isBlank()) throw new InvalidBearerTokenException("Invalid Bearer Token");
        return new UserIdAuthentication(token);
    }

}
