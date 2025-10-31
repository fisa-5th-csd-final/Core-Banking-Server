package com.fisa.bank.common.config.security.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fisa.bank.user.persistence.entity.id.UserId;

@Getter
@RequiredArgsConstructor
public final class CustomUserDetails implements UserDetails {
  private final UserId userId; // ← 도메인 식별자
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled = true;

  // UserDetails 필수 구현
  public boolean isAccountNonExpired() {
    return true;
  }

  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }
}
