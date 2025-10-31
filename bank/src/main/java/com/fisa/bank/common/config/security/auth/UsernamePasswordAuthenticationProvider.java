package com.fisa.bank.common.config.security.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("UsernamePassword")
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

  private final PasswordEncoder passwordEncoder;
  private final UserDetailsService userDetailsService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = (String) authentication.getPrincipal();
    String password = (String) authentication.getCredentials();
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    String encrypted = userDetails.getPassword();

    if (passwordEncoder.matches(password, encrypted)) {
      // 인증된 Authentication 반환
      return new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
    }

    throw new BadCredentialsException("Unauthorized");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
