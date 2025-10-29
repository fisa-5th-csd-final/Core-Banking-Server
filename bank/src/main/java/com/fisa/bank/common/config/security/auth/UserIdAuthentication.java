package com.fisa.bank.common.config.security.auth;

import com.fisa.bank.user.persistence.entity.id.UserId;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 내부에 UserId 를 가지고 있는 Authentication
 */
public class UserIdAuthentication extends AbstractAuthenticationToken {

    private final UserId userId;

    public UserIdAuthentication(UserId userId, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
