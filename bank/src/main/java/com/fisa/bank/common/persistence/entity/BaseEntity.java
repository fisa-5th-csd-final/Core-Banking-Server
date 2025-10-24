package com.fisa.bank.common.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@MappedSuperclass
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public BaseEntity(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null;
    }

    @PreRemove
    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

}
