package com.fisa.bank.domains.common.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 필터 정의
// @FilterDef(
//        name = "deletedFilter", // 정의할 필터 이름
//        parameters = @ParamDef(name = "isDeleted", type = Boolean.class) // 필터에 사용될 파라미터
// )
//// 실제 적용되는 핕터
// @Filter(
//        name = "deletedFilter", // 적용할 필터 이름
//        condition = "is_deleted = :isDeleted" // 필터 조건 - sql 실행 시 해당 조건에 따라 실행
// )
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public BaseEntity() {}

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }
}
