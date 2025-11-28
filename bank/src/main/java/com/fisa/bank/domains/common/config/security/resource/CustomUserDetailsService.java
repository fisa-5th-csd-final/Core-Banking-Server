package com.fisa.bank.domains.common.config.security.resource;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.enums.UserRole;
import com.fisa.bank.domains.user.persistence.repository.UserAuthRepository;

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

    Collection<SimpleGrantedAuthority> authorities = mapAuthorities(userAuth.getRole());

    return new User(username, userAuth.getPassword(), authorities);
  }

  private Collection<SimpleGrantedAuthority> mapAuthorities(UserRole role) {
    if (role == null) {
      return List.of();
    }
    return switch (role) {
      case ADMIN -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
      case USER -> List.of(new SimpleGrantedAuthority("ROLE_USER"));
    };
  }
}
