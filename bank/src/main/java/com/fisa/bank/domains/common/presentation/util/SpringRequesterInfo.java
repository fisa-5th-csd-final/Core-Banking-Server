package com.fisa.bank.domains.common.presentation.util;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fisa.bank.domains.common.application.util.RequesterInfo;
import com.fisa.bank.domains.common.config.security.resource.JwtAuthentication;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;

@Component
@RequiredArgsConstructor
public class SpringRequesterInfo implements RequesterInfo {

  /**
   * SecurityContextHolder에서 SecurityContext를 가져와서 Authentication을 추출한 후 Authentication에 저장된 UserId를
   * 반환
   *
   * @return
   */
  @Override
  public UserId getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (Objects.isNull(authentication)) {
      throw new IllegalStateException("Authentication should be not null");
    }

    if (!(authentication instanceof JwtAuthentication)) {
      throw new IllegalStateException("UserIdAuthentication not found");
    }

    return UserId.of((Long) authentication.getPrincipal());
  }
}
