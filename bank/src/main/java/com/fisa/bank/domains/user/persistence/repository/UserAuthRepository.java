package com.fisa.bank.domains.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisa.bank.domains.user.persistence.entity.UserAuth;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {

  @Query("SELECT ua.user.userId FROM UserAuth ua WHERE ua.loginId=:loginId")
  Optional<UserId> findUserIdByLoginId(@Param("loginId") String loginId);

  @Query("select ua from UserAuth ua where ua.user.userId = :userId")
  Optional<UserAuth> findByUserId(@Param("userId") UserId userId);
}
