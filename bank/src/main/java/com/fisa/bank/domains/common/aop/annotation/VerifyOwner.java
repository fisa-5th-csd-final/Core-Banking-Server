package com.fisa.bank.domains.common.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyOwner {
  DomainType domain(); // 도메인 구분용

  String idParam() default "userId"; // 메서드 또는 DTO에서 찾을 파라미터 이름
}
