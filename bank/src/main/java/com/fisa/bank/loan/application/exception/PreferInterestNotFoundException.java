package com.fisa.bank.loan.application.exception;

import com.fisa.bank.common.application.exception.BusinessException;
import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;

public class PreferInterestNotFoundException extends BusinessException {

  private static final String errorCode = "L002";
  private static final String message = "해당 우대금리가 없습니다. (신용등급: %s 고객등급: %s)";

  public PreferInterestNotFoundException(CreditRating creditRating, CustomerLevel customerLevel) {
    super(errorCode, String.format(message, creditRating, customerLevel));
  }
}
