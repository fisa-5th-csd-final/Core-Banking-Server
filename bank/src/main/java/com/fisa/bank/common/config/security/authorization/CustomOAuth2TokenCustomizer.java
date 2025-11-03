package com.fisa.bank.common.config.security.authorization;

import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserAuthRepository userAuthRepository;

    @Override
    public void customize(JwtEncodingContext context) {
        var principal = context.getPrincipal(); // 인증된 사용자(SecurityContext의 Authentication)

        if(principal instanceof UsernamePasswordAuthenticationToken){
            UserDetails user = (UserDetails) principal.getPrincipal();

            UserId userId = userAuthRepository.findUserIdByLoginId(user.getUsername())
                    .orElseThrow(() -> new AuthenticationServiceException("Not found username"));
            // 공통 정보
            var authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

            if (context.getTokenType().getValue().equals("access_token")) {
                // Access Token 커스텀
                context.getClaims().claim("roles", authorities);
                context.getClaims().claim("user_id", userId.getValue());
                // aud, jti, nbf 등도 여기서 세밀 제어 가능
            }

            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                // ID Token 커스텀 (OIDC 표준 + 도메인 확장)
            }
        }
    }

}
