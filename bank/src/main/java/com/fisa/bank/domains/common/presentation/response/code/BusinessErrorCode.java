package com.fisa.bank.domains.common.presentation.response.code;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;

import com.fisa.bank.domains.account.application.exception.*;
import com.fisa.bank.domains.account.application.exception.AccessDeniedException;
import com.fisa.bank.domains.account.application.exception.AccountNotDeletableException;
import com.fisa.bank.domains.account.application.exception.AccountNotFoundException;
import com.fisa.bank.domains.account.application.exception.AccountOwnerMismatchException;
import com.fisa.bank.domains.account.application.exception.InsufficientBalanceException;
import com.fisa.bank.domains.account.application.exception.InvalidTransferTargetException;
import com.fisa.bank.domains.common.application.exception.AlreadyDeletedException;
import com.fisa.bank.domains.common.application.exception.BusinessException;
import com.fisa.bank.domains.common.presentation.response.code.ApiResponseCode.ErrorResponseCode;
import com.fisa.bank.domains.interest.application.exception.InterestException;
import com.fisa.bank.domains.loan.application.exception.*;
import com.fisa.bank.domains.loan.application.exception.DuplicateLoanException;
import com.fisa.bank.domains.loan.application.exception.InsufficientRepaymentException;
import com.fisa.bank.domains.loan.application.exception.LoanLedgerAccessDeniedException;
import com.fisa.bank.domains.loan.application.exception.LoanLedgerNotFoundException;
import com.fisa.bank.domains.loan.application.exception.LoanProductNotDeletableException;
import com.fisa.bank.domains.loan.application.exception.LoanProductNotFoundException;
import com.fisa.bank.domains.loan.application.exception.PreferInterestNotFoundException;
import com.fisa.bank.domains.loan.application.exception.UnknownCalculatorException;
import com.fisa.bank.domains.user.application.exception.InvalidAuthInfoException;
import com.fisa.bank.domains.user.application.exception.InvalidPasswordFormatException;
import com.fisa.bank.domains.user.application.exception.UserNotFoundException;

public enum BusinessErrorCode implements ErrorResponseCode<BusinessException> {

  /** 여기에 커스텀 BusinessException을 정의하면 됩니다. */
  // Common
  ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, AlreadyDeletedException.class),
  // Auth
  INVALID_PASSWORD_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidPasswordFormatException.class),
  INVALID_AUTH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidAuthInfoException.class),
  // Account
  ACCOUNT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, AccountNotFoundException.class),
  INSUFFICIENT_BALANCE_EXCEPTION(HttpStatus.BAD_REQUEST, InsufficientBalanceException.class),
  ACCOUNT_OWNER_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, AccountOwnerMismatchException.class),
  ACCOUNT_NOT_DELETABLE_EXCEPTION(HttpStatus.BAD_REQUEST, AccountNotDeletableException.class),
  ACCESSS_DENIED_EXCEPTION(HttpStatus.BAD_REQUEST, AccessDeniedException.class),
  INVALID_TRANSFER_TARGET_EXCEPTION(HttpStatus.BAD_REQUEST, InvalidTransferTargetException.class),
  // Loan
  LOAN_PRODUCT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, LoanProductNotFoundException.class),
  DUPLICATE_LOAN_EXCEPTION(HttpStatus.CONFLICT, DuplicateLoanException.class),
  INSUFFICIENT_REPAYMENT_EXCEPTION(
      HttpStatus.UNPROCESSABLE_ENTITY, InsufficientRepaymentException.class),
  PREFER_INTEREST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, PreferInterestNotFoundException.class),
  LOAN_LEDGER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, LoanLedgerNotFoundException.class),
  LOAN_LEDGER_ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, LoanLedgerAccessDeniedException.class),
  UNKNOWN_CALCULATOR_EXCEPTION(HttpStatus.UNPROCESSABLE_ENTITY, UnknownCalculatorException.class),
  LOAN_PRODUCT_NOT_DELETABLE_EXCEPTION(
      HttpStatus.BAD_REQUEST, LoanProductNotDeletableException.class),
  // Interest
  INTEREST_EXCEPTION(HttpStatus.NOT_FOUND, InterestException.class),

  // user
  USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, UserNotFoundException.class);

  private final HttpStatus status;
  @Getter private final Class<? extends BusinessException> exception;

  BusinessErrorCode(HttpStatus status, Class<? extends BusinessException> eClass) {
    this.status = status;
    this.exception = eClass;
  }

  @Override
  public HttpStatus getStatus() {
    return this.status;
  }

  @Override
  public String getCode() {
    throw new UnsupportedOperationException(
        "You should use errorCode, message in the BusinessException");
  }

  @Override
  public String getMessage() {
    throw new UnsupportedOperationException(
        "You should use errorCode, message in the BusinessException");
  }

  public static ErrorResponseCode<BusinessException> find(BusinessException exception) {
    BusinessErrorCode errorResponseCode = map.get(exception.getClass());

    if (errorResponseCode != null) return errorResponseCode;

    throw new IllegalArgumentException("Not mapped Exception");
  }

  /** 커스텀 Exception에 맞는 ErrorCode를 매핑하는 저장소 */
  private static final Map<Class<? extends BusinessException>, BusinessErrorCode> map =
      new ConcurrentHashMap<>();

  static {
    Arrays.stream(BusinessErrorCode.values())
        .forEach(
            errorCode -> {
              Class<? extends BusinessException> eClass = errorCode.exception;
              map.put(eClass, errorCode);
            });
  }
}
