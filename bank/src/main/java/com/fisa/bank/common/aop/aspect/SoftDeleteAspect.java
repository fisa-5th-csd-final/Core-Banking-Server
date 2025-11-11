package com.fisa.bank.common.aop.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.common.persistence.entity.BaseEntity;
import com.fisa.bank.interest.persistence.entity.InterestRate;
import com.fisa.bank.loan.persistence.entity.LoanProduct;

@Aspect
@Component
@RequiredArgsConstructor
public class SoftDeleteAspect {

  @PersistenceContext private final EntityManager entityManager;

  /** JpaRepository의 delete() 메서드 호출을 가로채 soft delete로 대체 */
  @Around("execution(* org.springframework.data.jpa.repository.JpaRepository.delete(..))")
  @Transactional
  public Object handleSoftDelete(ProceedingJoinPoint joinPoint) throws Throwable {
    Object arg = joinPoint.getArgs()[0];

    if (arg instanceof BaseEntity baseEntity) {

      // LoanProduct라면 연관된 InterestRate 리스트도 delete 호출
      if (baseEntity instanceof LoanProduct loanProduct) {
        if (loanProduct.getInterestRateList() != null) {
          for (InterestRate ir : loanProduct.getInterestRateList()) {
            ir.delete();
            entityManager.merge(ir);
          }
        }
      }

      baseEntity.delete(); // BaseEntity의 delete() 호출
      entityManager.merge(baseEntity); // UPDATE 실행
      return null; // 실제 delete()는 수행하지 않음
    }

    // BaseEntity가 아닐 경우 원래 동작 수행
    return joinPoint.proceed();
  }
}
