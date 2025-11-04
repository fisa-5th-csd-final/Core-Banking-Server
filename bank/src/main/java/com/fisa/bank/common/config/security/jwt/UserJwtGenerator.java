package com.fisa.bank.common.config.security.jwt;

import static com.fisa.bank.common.config.security.jwt.JwtConst.CLAIM_ISSUER;
import static com.fisa.bank.common.config.security.jwt.JwtConst.CLAIM_ROLE;
import static com.fisa.bank.common.config.security.jwt.JwtConst.CLAIM_USER_ID;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class UserJwtGenerator {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");
  private static final JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

  private final JwtProperties jwtProperties;
  private final JwtEncoder jwtEncoder;

  public UserJwtGenerator(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
    this.jwtProperties = jwtProperties;
  }

  public Jwt createAccessToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
    ZonedDateTime now = LocalDateTime.now().atZone(KST);
    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .claim(CLAIM_USER_ID, userId)
            .claim(CLAIM_ROLE, authorities)
            .issuer(CLAIM_ISSUER)
            .issuedAt(now.toInstant())
            .expiresAt(now.toInstant().plus(jwtProperties.getAccessTokenExpiration()))
            .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet));
  }

  public Jwt createRefreshToken(Long userId) {
    ZonedDateTime now = LocalDateTime.now().atZone(KST);
    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .claim(CLAIM_USER_ID, userId)
            .issuer(CLAIM_ISSUER)
            .issuedAt(now.toInstant())
            .expiresAt(now.toInstant().plus(jwtProperties.getRefreshTokenExpiration()))
            .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet));
  }
}
