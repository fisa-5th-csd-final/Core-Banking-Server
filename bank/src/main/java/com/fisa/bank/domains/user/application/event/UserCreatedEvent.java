package com.fisa.bank.domains.user.application.event;

import com.fisa.bank.domains.user.persistence.entity.id.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreatedEvent {

    private final UserId userId;

}
