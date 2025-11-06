package com.fisa.bank.common.config.security.resource;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserAuthRepository authRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserAuth userAuth =
        authRepository
            .findById(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        String.format("Username not found : %s", username)));

    return new User(username, userAuth.getPassword(), Collections.emptyList());
  }
}
