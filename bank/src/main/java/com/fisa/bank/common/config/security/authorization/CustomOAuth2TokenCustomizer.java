package com.fisa.bank.common.config.security.authorization;

import static com.fisa.bank.common.config.security.authorization.OAuth2Const.OAUTH2_ACCESS_TOKEN;
import static com.fisa.bank.common.config.security.jwt.JwtConst.*;

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

import com.fisa.bank.user.persistence.entity.id.UserId;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;

/** OAuth2 Client에게 AccessToken을 발급할 때, Jwt에 사용자의 UserId 클레임을 삽입하는 역할을 수행 */

/**
 * OAuth2TokenCustomizer가 호출되는 시점 1. Client 동적 등록을 위해, ClientRegistrar 로 인증을 수행할 경우에, 해당 클라이언트에 대한
 * 인증 결과로 액세스 토큰을 발급한다. 이때 AccessToken을 발급하기 위해, OAuth2TokenGenerator가 Customizer를 전부 호출한다.
 *
 * <p>2. 사용자가 로그인을 수행하고, OIDC 정보 제공 동의를 했을 때 클라이언트에게 AccessToken을 발급한다. 이때 사용자가 로그인을 수행했으니까,
 * UsernamePasswordAuthentication 객체가 만들어지는데, 이떄도 또한 TokenGenerator가 Customizer를 호출한다. 사용자가 로그인을 했을
 * 경우에는 UserId 를 담기 위해서 이 Customizer 에서 Claim을 커스텀하는 과정을 거친다.
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
    if (principal instanceof UsernamePasswordAuthenticationToken) {
      UserDetails user = (UserDetails) principal.getPrincipal();

      UserId userId =
          userAuthRepository
              .findUserIdByLoginId(user.getUsername())
              .orElseThrow(() -> new AuthenticationServiceException("Not found username"));
      // 공통 정보
      var authorities =
          principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

      if (context.getTokenType().getValue().equals(OAUTH2_ACCESS_TOKEN)) {
        // Access Token 커스텀
        // aud, jti, nbf 등도 여기서 세밀 제어 가능
        context.getClaims().claim(CLAIM_ROLE, authorities);
        context.getClaims().claim(CLAIM_USER_ID, userId.getValue());
      }

      if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
        // ID Token 커스텀 (OIDC 표준 + 도메인 확장)
      }
    }
    // UsernamePassword Authentication이 아니라면 그냥 통과
  }
}
