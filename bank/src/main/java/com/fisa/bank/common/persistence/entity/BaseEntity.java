package com.fisa.bank.common.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

  // TODO: Auditing Listener 적용

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  protected LocalDateTime deletedAt;
  protected boolean isDeleted;

  public BaseEntity() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.deletedAt = null;
    this.isDeleted = false;
  }

  public void delete() {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
  }
}
