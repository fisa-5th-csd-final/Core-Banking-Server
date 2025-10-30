package com.fisa.bank.common.config.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    public JwtGenerator(
            JwtProperties jwtProperties,
            @Qualifier("AppJwtEncoder") JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public Jwt createAccessToken(Long userId, Collection<? extends GrantedAuthority> authorities){
        ZonedDateTime now = LocalDateTime.now().atZone(KST);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .claim("userId", userId)
                .claim("role", authorities)
                .issuer("core-bank")
                .issuedAt(now.toInstant())
                .expiresAt(now.toInstant().plus(jwtProperties.getAccessTokenExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
    }

    public Jwt createRefreshToken(Long userId){
        ZonedDateTime now = LocalDateTime.now().atZone(KST);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .claim("userId", userId)
                .issuer("core-bank")
                .issuedAt(now.toInstant())
                .expiresAt(now.toInstant().plus(jwtProperties.getRefreshTokenExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
    }

}
