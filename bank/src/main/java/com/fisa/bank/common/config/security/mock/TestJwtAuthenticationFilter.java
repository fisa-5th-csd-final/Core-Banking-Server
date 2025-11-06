package com.fisa.bank.common.config.security.mock;

import com.fisa.bank.common.config.security.resource.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

/**
 * 테스트 환경에서 사용되는 Jwt 필터
 * Authorization 헤더에 jwt 토큰을 담아서 보내면, Authentication 으로 만들고
 * jwt 토큰이 없어도, Authentication 으로 만든다.
 */
@Slf4j
public class TestJwtAuthenticationFilter extends AuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationConverter authenticationConverter;

    public TestJwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationConverter authenticationConverter) {
        super(authenticationManager, authenticationConverter);
        this.authenticationManager = authenticationManager;
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (super.getRequestMatcher().matches(request)) {
            // Authentication 변환
            Authentication authentication = authenticationConverter.convert(request);

            if (Objects.nonNull(authentication)) {
                try {
                    // 인증 진행
                    authentication = authenticationManager.authenticate(authentication);
                    // 컨텍스트 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (AuthenticationException e) {
                    log.warn("인증 예외 발생 : {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                } catch (Exception e) {
                    log.warn("예상하지 못한 예외 발생 : {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            }

            else{
                // 빈 Authentication 객체 세팅
                authentication = new JwtAuthentication(null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}


