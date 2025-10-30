package com.fisa.bank.common.config.security.jwt;

import com.fisa.bank.common.config.security.util.AsymmetricKeyUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final AccessToken accessToken;
    private final RefreshToken refreshToken;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    @ConstructorBinding
    public JwtProperties(AccessToken accessToken, RefreshToken refreshToken, String publicKey, String privateKey){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.privateKey = AsymmetricKeyUtils.createPrivateKey(privateKey, "RSA");
        this.publicKey = AsymmetricKeyUtils.createPublicKey(publicKey, "RSA");
    }

    public record AccessToken(Duration expiry) {
    }

    public record RefreshToken(Duration expiry) {
    }

    public Duration getAccessTokenExpiration(){ return this.accessToken.expiry; }
    public Duration getRefreshTokenExpiration(){ return this.refreshToken.expiry; }

}
