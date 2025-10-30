package com.fisa.bank.common.config.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final AccessToken accessToken;
    private final RefreshToken refreshToken;
    private final SecretKey secretKey; // TODO: at, rt 의 secret을 다르게 해야 할까?

    @ConstructorBinding
    public JwtProperties(AccessToken accessToken, RefreshToken refreshToken, String secretKey){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }


    public record AccessToken(Duration expiry) {}
    public record RefreshToken(Duration expiry) {}

    public Duration getAccessTokenExpiration(){ return this.accessToken.expiry; }
    public Duration getRefreshTokenExpiration(){ return this.refreshToken.expiry; }

}
