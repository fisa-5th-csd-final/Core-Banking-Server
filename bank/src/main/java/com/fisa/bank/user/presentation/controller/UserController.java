package com.fisa.bank.user.presentation.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.common.presentation.response.ApiResponse;
import com.fisa.bank.common.presentation.response.ApiResponseGenerator;
import com.fisa.bank.common.presentation.response.body.SuccessBody;
import com.fisa.bank.common.presentation.response.code.ResponseCode;
import com.fisa.bank.user.application.dto.UserCreateRequest;
import com.fisa.bank.user.application.dto.UserInfoResponse;
import com.fisa.bank.user.application.service.UserService;
import com.fisa.bank.user.persistence.entity.id.UserId;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final RequesterInfo requesterInfo;

  @PostMapping
  public ApiResponse<SuccessBody<Void>> signUp(@RequestBody UserCreateRequest request) {
    boolean success = userService.create(request);

    return ApiResponseGenerator.success(ResponseCode.CREATE);
  }

  @GetMapping("/me")
  public ApiResponse<SuccessBody<UserInfoResponse>> myInfo() {
    // TODO: presentation 에서 UserId 를 알고 있는 게 맞는 걸까?
    UserId userId = requesterInfo.getUserId();
    return ApiResponseGenerator.success(ResponseCode.GET, userService.getUserInfo(userId));
  }
}
