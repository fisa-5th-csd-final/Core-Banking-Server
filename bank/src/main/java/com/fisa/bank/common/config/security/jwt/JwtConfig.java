package com.fisa.bank.common.config.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
    private static final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;

    private final JwkProperties jwkProperties;


    // 전역 JwtDecoder AuthorizationServer나 ResourceServer나 같은 JwtDecoder를 사용한다.
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/oauth2/jwks") // 공개키롤 받아오기 위함
                .jwsAlgorithm(signatureAlgorithm) // 서명 알고리즘
                .build();
    }

    // 전역 JwtEncoder AuthorizationServer나 ResourceServer나 같은 JwtEncoder를 사용한다.
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    // JWT에 사용되는 비밀키, 공개키 정보 빈으로 등록
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAPublicKey publicKey = (RSAPublicKey) jwkProperties.getPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) jwkProperties.getPrivateKey();
        RSAKey rsaKey =
                new RSAKey.Builder(publicKey)
                        .privateKey(privateKey)
                        .keyID(UUID.randomUUID().toString()) // KeyID 설정 -> Key 회전 가능
                        .algorithm(jwsAlgorithm) // -> Jws 서명 알고리즘을 이걸로 사용하겠다는 설정
                        .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
}
