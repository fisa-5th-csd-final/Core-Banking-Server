package com.fisa.bank.domains.user.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisa.bank.domains.user.persistence.entity.User;
import com.fisa.bank.domains.user.persistence.entity.id.UserId;

public interface UserRepository extends JpaRepository<User, UserId> {

  @EntityGraph(attributePaths = {"userAuth"})
  List<User> findAll();
}
