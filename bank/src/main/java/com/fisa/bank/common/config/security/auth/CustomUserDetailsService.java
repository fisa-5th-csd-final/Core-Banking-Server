package com.fisa.bank.common.config.security.auth;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.UserAuth;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;

@Component
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

    User user = userAuth.getUser();

    return new CustomUserDetails(
        user.getUserId(), userAuth.getLoginId(), userAuth.getPassword(), Collections.emptyList());
  }
}
