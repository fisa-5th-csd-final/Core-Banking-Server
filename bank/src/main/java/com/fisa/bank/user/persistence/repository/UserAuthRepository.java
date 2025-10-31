package com.fisa.bank.user.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.user.persistence.entity.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {}
