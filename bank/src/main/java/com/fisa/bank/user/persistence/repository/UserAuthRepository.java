package com.fisa.bank.user.persistence.repository;

import com.fisa.bank.user.persistence.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
}
