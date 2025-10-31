package com.fisa.bank.common.config.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {

  @Bean("AppJwtDecoder")
  public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
    return NimbusJwtDecoder.withSecretKey(jwtProperties.getSecretKey())
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  @Bean("AppJwtEncoder")
  public JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(jwtProperties.getSecretKey()));
  }
}
