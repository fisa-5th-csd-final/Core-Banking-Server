package com.fisa.bank.common.event;

import com.fisa.bank.common.persistence.id.BaseId;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class DomainEvent implements Event{

    private final BaseId<?> domainId;
    private final LocalDateTime createdAt;

    public DomainEvent(BaseId<?> id){
        this.domainId = id;
        this.createdAt = LocalDateTime.now();
    }

}
