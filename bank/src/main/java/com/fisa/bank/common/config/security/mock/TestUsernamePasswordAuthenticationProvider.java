package com.fisa.bank.common.config.security.mock;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class TestUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String userId = (String) authentication.getPrincipal();
    String password = (String) authentication.getCredentials();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    return new UsernamePasswordAuthenticationToken(
        new User(userId, password, authorities), password, authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
