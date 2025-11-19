package com.fisa.bank.domains.common.config.security.mock;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/** 테스트용 UserDetailsService */
public class TestUserDetailsService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    return new User(userId, "{noop}password", Collections.emptyList());
  }
}
