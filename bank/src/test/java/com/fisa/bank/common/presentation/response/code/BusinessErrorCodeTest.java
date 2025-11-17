package com.fisa.bank.common.presentation.response.code;

import static org.assertj.core.api.Assertions.assertThat;

import com.fisa.bank.domains.common.presentation.response.code.BusinessErrorCode;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import com.fisa.bank.domains.common.application.exception.BusinessException;

class BusinessErrorCodeTest {

  @Test
  @DisplayName("모든 BusinessException의 하위 예외는 BusinessErrorCode에 등록되어야 한다.")
  void everyBusinessExceptionIsMappedInBusinessErrorCode() throws ClassNotFoundException {
    Set<Class<? extends BusinessException>> registeredExceptions =
        Arrays.stream(BusinessErrorCode.values())
            .map(BusinessErrorCode::getException)
            .collect(Collectors.toSet());

    Set<Class<? extends BusinessException>> concreteBusinessExceptions =
        scanConcreteBusinessExceptions();

    assertThat(registeredExceptions)
        .as("BusinessErrorCode should register all BusinessException implementations")
        .containsExactlyInAnyOrderElementsOf(concreteBusinessExceptions);
  }

  private Set<Class<? extends BusinessException>> scanConcreteBusinessExceptions()
      throws ClassNotFoundException {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(BusinessException.class));

    Set<Class<? extends BusinessException>> result = new HashSet<>();
    ClassLoader classLoader = getClass().getClassLoader();

    for (BeanDefinition candidate : scanner.findCandidateComponents("com.fisa.bank")) {
      Class<?> clazz = ClassUtils.forName(candidate.getBeanClassName(), classLoader);
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        result.add(clazz.asSubclass(BusinessException.class));
      }
    }

    return result;
  }
}
