package com.fisa.bank.common.config.security.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 핸들러
 * 스프링 시큐리티에 의해, 사용자 인증이 성공하면
 * Authentication 객체를 Jwt 토큰으로 인코딩하여
 * ResponseBody에 담는다.
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (Objects.nonNull(userDetails)) {
                Long userId = userDetails.getUserId().getValue();
                ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul"));

                JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                        .claim("userId", userId)
                        .issuer("core-bank")
                        .issuedAt(now.toInstant())
                        .expiresAt(now.plusHours(1).toInstant())
                        .build();

                JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                        .claim("userId", userId)
                        .issuer("core-bank")
                        .issuedAt(now.toInstant())
                        .expiresAt(now.plusHours(6).toInstant())
                        .build();

                String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
                String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

                // body에 담기
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter()
                        .write(createBody(accessToken, refreshToken));
                response.flushBuffer();
                return;
            }
            throw new IllegalStateException("UserDetails should be not null");
        }

        // UserIdAuthentication 이 아니면 예외
        throw new IllegalStateException("Authentication is not UserIdAuthentication");

    }

    // AccessToken, RefreshToken 이 담긴 바디를 만드는 과정
    private String createBody(String accessToken, String refreshToken){
        try {
            return objectMapper.writeValueAsString(Map.of("access_token", accessToken, "refresh_token", refreshToken));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Exception occur in json processing");
        }
    }
}
