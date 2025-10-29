package com.fisa.bank.common.presentation.util;

import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.user.persistence.entity.id.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringRequesterInfo implements RequesterInfo {

    /**
     * SecurityContextHolder에서 SecurityContext를 가져와서
     * Authentication을 추출한 후
     * Authentication에 저장된 UserId를 반환
     * @return
     */
    @Override
    public UserId getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof UsernamePasswordAuthenticationToken){
            return (UserId) authentication.getPrincipal();
        }

        // Security Context에 저장된 Authentication 이 존재하지 않을 경우 발생하는 에러
        throw new IllegalStateException("Authentication not found");
    }
}
