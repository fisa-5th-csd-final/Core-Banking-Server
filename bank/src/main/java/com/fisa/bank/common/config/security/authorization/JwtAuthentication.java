package com.fisa.bank.common.config.security.authorization;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/** 내부에 UserId 를 가지고 있는 Authentication */
public class JwtAuthentication extends AbstractAuthenticationToken {

  // jwt token
  private final String credentials;
  private final Long userId;

  public JwtAuthentication(String credentials) {
    super(null);
    this.userId = null;
    this.credentials = credentials;
  }

  public JwtAuthentication(Long userId, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    super.setAuthenticated(true);
    this.userId = userId;
    this.credentials = null;
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.userId;
  }
}
