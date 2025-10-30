package com.fisa.bank.common.config.security.auth;

import com.fisa.bank.user.persistence.entity.id.UserId;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * 클라이언트가 보낸 Jwt 토큰을 Authentication 으로 변환해주는 역할
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Long userId = source.getClaim("userId");
        Collection<? extends GrantedAuthority> authorities = source.getClaim("role");

        AbstractAuthenticationToken token = new UserIdAuthentication(UserId.of(userId), authorities);

        token.setAuthenticated(true);

        return token;
    }
}
