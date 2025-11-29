package com.fisa.bank.domains.admin.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisa.bank.domains.admin.application.dto.AdminUserSummaryResponse;
import com.fisa.bank.domains.user.persistence.repository.UserRepository;

@Service
public class AdminUserService {

  private final UserRepository userRepository;

  public AdminUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<AdminUserSummaryResponse> getUsers() {
    return userRepository.findAllNonAdmin().stream()
        .map(
            user ->
                new AdminUserSummaryResponse(
                    user.getUserId().getValue(), user.getName(), user.getUserAuth().getLoginId()))
        .toList();
  }
}
