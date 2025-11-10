package com.fisa.bank.common.aop.aspect;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.fisa.bank.account.application.exception.AccessDeniedException;
import com.fisa.bank.account.application.service.reader.AccountReader;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.common.aop.annotation.VerifyOwner;
import com.fisa.bank.common.application.util.RequesterInfo;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

  private final AccountReader accountReader;
  private final RequesterInfo requesterInfo;

  @Before("@annotation(verifyOwner)")
  public void verifyOwnership(JoinPoint joinPoint, VerifyOwner verifyOwner) {
    Object idValue = extractParamValue(joinPoint, verifyOwner.idParam());

    switch (verifyOwner.domain()) {
      case ACCOUNT -> {
        Account account = accountReader.getAccountByAccountNumber(idValue.toString());
        if (!account
            .getUser()
            .getUserId()
            .getValue()
            .equals(requesterInfo.getUserId().getValue())) {
          throw new AccessDeniedException();
        }
      }
      case LOAN -> {}
    }
  }

  private Object extractParamValue(JoinPoint joinPoint, String paramName) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < parameterNames.length; i++) {
      if (parameterNames[i].equals(paramName)) {
        return args[i];
      }
    }

    for (Object arg : args) {
      if (arg == null) continue;
      try {
        Field field = arg.getClass().getDeclaredField(paramName);
        field.setAccessible(true);
        return field.get(arg);
      } catch (NoSuchFieldException ignored) {
        // Not in this arg, continue.
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Failed to access field: " + paramName, e);
      }
    }
    throw new IllegalArgumentException(paramName + " 파라미터를 찾을 수 없습니다.");
  }
}
