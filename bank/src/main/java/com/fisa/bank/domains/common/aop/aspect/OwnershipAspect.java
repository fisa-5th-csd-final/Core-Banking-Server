package com.fisa.bank.domains.common.aop.aspect;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fisa.bank.domains.account.application.exception.AccessDeniedException;
import com.fisa.bank.domains.account.application.service.reader.AccountReader;
import com.fisa.bank.domains.account.persistence.entity.Account;
import com.fisa.bank.domains.common.aop.annotation.VerifyOwner;
import com.fisa.bank.domains.common.application.util.RequesterInfo;
import com.fisa.bank.domains.loan.application.exception.LoanLedgerAccessDeniedException;
import com.fisa.bank.domains.loan.application.service.reader.LoanReader;
import com.fisa.bank.domains.loan.persistence.entity.LoanLedger;

@Aspect
@Component
@ConditionalOnExpression("!'${spring.profiles.active:}'.contains('test')")
@RequiredArgsConstructor
public class OwnershipAspect {

  private final AccountReader accountReader;
  private final LoanReader loanReader;
  private final RequesterInfo requesterInfo;

  @Before("@annotation(verifyOwner)")
  public void verifyOwnership(JoinPoint joinPoint, VerifyOwner verifyOwner) {
    if (isAdmin()) {
      return; // ADMIN은 소유권 검사를 건너뜀
    }

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
      case LOAN -> {
        Long loanLedgerId = ((Number) idValue).longValue();
        LoanLedger loanLedger = loanReader.findLoanLedgerById(loanLedgerId);
        if (!loanLedger
            .getUser()
            .getUserId()
            .getValue()
            .equals(requesterInfo.getUserId().getValue())) {
          throw new LoanLedgerAccessDeniedException();
        }
      }
    }
  }

  private boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getAuthorities() == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
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
