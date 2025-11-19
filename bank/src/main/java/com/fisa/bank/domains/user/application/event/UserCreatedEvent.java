package com.fisa.bank.domains.user.application.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreatedEvent {

    // 이벤트는 외부로 전달해야 할 수도 있으므로 Long
    private final Long userId;

}
