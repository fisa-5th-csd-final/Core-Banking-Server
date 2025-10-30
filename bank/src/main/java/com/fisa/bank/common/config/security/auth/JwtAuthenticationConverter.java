package com.fisa.bank.common.config.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * 클라이언트가 보낸 Jwt 토큰을 Authentication 으로 변환해주는 역할
 * Jwt 토큰이 유효하면, 바로 인증완료를 수행한다.
 */
@Component("AppAuthenticationConverter")
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationConverter(@Qualifier("AppJwtDecoder") JwtDecoder jwtDecoder){
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

        if(token.isBlank()) throw new InvalidBearerTokenException("Invalid Bearer Token");

        try {
            Jwt jwt = jwtDecoder.decode(token);

            Long userId = jwt.getClaim("userId");
            Collection<? extends GrantedAuthority> authorities = jwt.getClaim("role");

            return new UserIdAuthentication(userId, authorities);
        } catch (JwtValidationException e){
            throw new AccountExpiredException("Token is Expired", e);
        } catch (BadJwtException e){
            throw new BadCredentialsException("Bad Jwt Exception", e);
        } catch (JwtEncodingException e){
            throw new InvalidBearerTokenException("Jwt format is invalid", e);
        } catch (Exception e){
            throw new AuthenticationServiceException("Unexpected Exception occurred", e);
        }
    }

}
