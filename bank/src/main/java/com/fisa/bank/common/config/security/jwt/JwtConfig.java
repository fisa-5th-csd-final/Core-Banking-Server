package com.fisa.bank.common.config.security.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    @Bean(name = "AppJwtDecoder")
    public JwtDecoder jwtDecoder(JwtProperties jwtProperties){
        return NimbusJwtDecoder
                .withSecretKey(jwtProperties.getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean(name = "AppJwtEncoder")
    public JwtEncoder jwtEncoder(JwtProperties jwtProperties){
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtProperties.getSecretKey()));
    }

}
