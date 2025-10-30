package com.fisa.bank.common.application.util;


import com.fisa.bank.user.persistence.entity.id.UserId;

// 요청한 사용자의 Id를 얻을 수 있는 인터페이스
public interface RequesterInfo {

    UserId getUserId();

}
