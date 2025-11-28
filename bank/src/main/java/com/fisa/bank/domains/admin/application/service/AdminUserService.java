package com.fisa.bank.domains.admin.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fisa.bank.domains.admin.application.dto.AdminUserSummaryResponse;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

@Service
public class AdminUserService {

  private final UserRepository userRepository;

  public AdminUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<AdminUserSummaryResponse> getUsers() {
    return userRepository.findAll().stream()
        .map(user -> new AdminUserSummaryResponse(user.getUserId().getValue(), user.getName(), user.getUserAuth().getLoginId()))
        .toList();
  }
}

