package com.fisa.bank.domains.user.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fisa.bank.domains.user.persistence.enums.UserRole;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserAuth {

  @Id private String loginId;
  private String password;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @OneToOne(mappedBy = "userAuth")
  private User user;

  public UserAuth(String loginId, String password) {
    this(loginId, password, UserRole.USER);
  }

  public UserAuth(String loginId, String password, UserRole role) {
    this.loginId = loginId;
    this.password = password;
    this.role = role;
  }

  public static UserAuth create(String loginId, String password) {
    return new UserAuth(loginId, password, UserRole.USER);
  }

  public static UserAuth createAdmin(String loginId, String password) {
    return new UserAuth(loginId, password, UserRole.ADMIN);
  }
}
