package com.fisa.bank.common.config.security.authorization;

import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import static com.fisa.bank.common.config.security.authorization.OAuth2Const.OAUTH2_ACCESS_TOKEN;
import static com.fisa.bank.common.config.security.jwt.JwtConst.*;

/**
 * OAuth2 Client에게 AccessToken을 발급할 때, Jwt에 사용자의 UserId 클레임을 삽입하는 역할을 수행
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserAuthRepository userAuthRepository;

    @Override
    public void customize(JwtEncodingContext context) {
        var principal = context.getPrincipal(); // 인증된 사용자(SecurityContext의 Authentication)

        // 사용자가 /login 페이지에서 form login 으로 인증을 수행했다면, UsernamePassword 인증 객체가 들어오는 게 맞다.
        if(principal instanceof UsernamePasswordAuthenticationToken){
            UserDetails user = (UserDetails) principal.getPrincipal();

            UserId userId = userAuthRepository.findUserIdByLoginId(user.getUsername())
                    .orElseThrow(() -> new AuthenticationServiceException("Not found username"));
            // 공통 정보
            var authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

            if (context.getTokenType().getValue().equals(OAUTH2_ACCESS_TOKEN)) {
                // Access Token 커스텀
                // aud, jti, nbf 등도 여기서 세밀 제어 가능
                context.getClaims().claim(CLAIM_ROLE, authorities);
                context.getClaims().claim(CLAIM_USER_ID, userId.getValue());
            }

            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                // ID Token 커스텀 (OIDC 표준 + 도메인 확장)
            }
            return;
        }

        log.warn("Accepted Authentication {}", principal);
        throw new AuthenticationServiceException("Principal should be UsernamePasswordAuthenticationToken");
    }

}
