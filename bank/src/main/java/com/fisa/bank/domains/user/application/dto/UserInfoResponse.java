package com.fisa.bank.domains.user.application.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fisa.bank.domains.user.persistence.entity.CreditRating;
import com.fisa.bank.domains.user.persistence.entity.CustomerLevel;
import com.fisa.bank.domains.user.persistence.entity.User;

public record UserInfoResponse(
    Long userId,
    String name,
    String address,
    LocalDateTime birthday,
    String job,
    BigInteger income,
    CreditRating creditLevel,
    CustomerLevel customerLevel) {

  public static UserInfoResponse of(User user) {
    return new UserInfoResponse(
        user.getUserId().getValue(),
        user.getName(),
        user.getAddress(),
        user.getBirthday(),
        user.getJob(),
        user.getIncome(),
        user.getCreditLevel(),
        user.getCustomerLevel());
  }
}
