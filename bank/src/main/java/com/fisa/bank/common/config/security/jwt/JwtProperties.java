package com.fisa.bank.common.config.security.jwt;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@Setter
@ConfigurationProperties(prefix="jwt")
public class JwtProperties {

    private final AccessToken accessToken;
    private final RefreshToken refreshToken;

    @ConstructorBinding
    public JwtProperties(AccessToken accessToken, RefreshToken refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public record AccessToken(Duration expiry) {
    }

    public record RefreshToken(Duration expiry) {
    }

    public Duration getAccessTokenExpiration(){ return this.accessToken.expiry; }
    public Duration getRefreshTokenExpiration(){ return this.refreshToken.expiry; }

}
