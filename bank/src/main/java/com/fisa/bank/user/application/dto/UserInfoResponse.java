package com.fisa.bank.user.application.dto;

import com.fisa.bank.user.persistence.entity.CreditRating;
import com.fisa.bank.user.persistence.entity.CustomerLevel;
import com.fisa.bank.user.persistence.entity.User;

public record UserInfoResponse(
    Long userId,
    String name,
    String address,
    String job,
    CreditRating creditLevel,
    CustomerLevel customerLevel) {

  public static UserInfoResponse of(User user) {
    return new UserInfoResponse(
        user.getUserId().getValue(),
        user.getName(),
        user.getAddress(),
        user.getJob(),
        user.getCreditLevel(),
        user.getCustomerLevel());
  }
}
