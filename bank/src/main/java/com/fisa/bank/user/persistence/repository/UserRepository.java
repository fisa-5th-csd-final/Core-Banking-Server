package com.fisa.bank.user.persistence.repository;

import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserId> {
}
