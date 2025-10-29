package com.fisa.bank.common.config.oauth2;

import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.repository.UserAuthRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserAuthRepository authRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (Objects.nonNull(userDetails)) {
                Long userId = getUserId(userDetails.getUsername());
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
                        .write("""
                                {"access_token":"%s","refresh_token":"%s"}
                                """.formatted(accessToken, refreshToken));
                response.flushBuffer();
                return;
            }
            throw new IllegalStateException("UserDetails should be not null");
        }

        // UserIdAuthentication 이 아니면 예외
        throw new IllegalStateException("Authentication is not UserIdAuthentication");

    }

    private Long getUserId(String loginId){
        User user = authRepository.findById(loginId)
                .orElseThrow(UserNotFoundException::new)
                .getUser();

        return user.getUserId().getValue();
    }

}
