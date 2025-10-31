package com.fisa.bank.common.config.security.authorization;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

/** UserIdAuthentication의 인증을 수행하는 Provider */
@Component("AppAuthenticationProvider")
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtDecoder jwtDecoder;

  public JwtAuthenticationProvider(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String token = (String) authentication.getCredentials();

    try {
      Jwt jwt = jwtDecoder.decode(token);
      Long userId = jwt.getClaim("userId");
      List<String> roles =
          jwt.getClaim("role") == null ? Collections.emptyList() : jwt.getClaim("role");
      Collection<? extends GrantedAuthority> authorities =
          roles.stream().map(SimpleGrantedAuthority::new).toList();

      return new JwtAuthentication(userId, authorities); // 자동으로 authenticated = true
    } catch (JwtValidationException e) {
      throw new AccountExpiredException("Token is Expired", e);
    } catch (BadJwtException e) {
      throw new BadCredentialsException("Bad Jwt Exception", e);
    } catch (JwtEncodingException e) {
      throw new InvalidBearerTokenException("Jwt format is invalid", e);
    } catch (Exception e) {
      throw new AuthenticationServiceException("Unexpected Exception occurred", e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(JwtAuthentication.class);
  }
}
