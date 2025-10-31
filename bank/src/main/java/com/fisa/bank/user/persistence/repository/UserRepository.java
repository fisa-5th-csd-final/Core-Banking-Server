package com.fisa.bank.user.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;

public interface UserRepository extends JpaRepository<User, UserId> {}
