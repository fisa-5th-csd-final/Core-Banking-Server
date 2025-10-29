package com.fisa.bank.common.config.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

// OAuth2.0 Authorization Server 를 설정하는 Config
@Configuration
public class AuthorizationConfig {


    // OAuth 2.0 클라이언트 저장소 등록
    // 인메모리, JDBC 선택 가능
    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbc) {
        return new JdbcRegisteredClientRepository(jdbc);
    }

    // 인증/인가 동의 저장소
    @Bean
    OAuth2AuthorizationService authorizationService(JdbcTemplate jdbc,
                                                    RegisteredClientRepository repo) {
        return new JdbcOAuth2AuthorizationService(jdbc, repo);
    }

    @Bean
    OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbc,
                                                                  RegisteredClientRepository repo) {
        return new JdbcOAuth2AuthorizationConsentService(jdbc, repo);
    }

    // Spring Security 의 AuthenticationManger 등록
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 사용자로부터 자격 증명 (ID/PW)를 받고
     * 인증을 수행하는 필터
     */
    @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager,
                                                     AuthenticationConverter authenticationConverter,
                                                     AuthenticationSuccessHandler successHandler,
                                                     AuthenticationFailureHandler failureHandler){

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, authenticationConverter);
        RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/login");

        authenticationFilter.setRequestMatcher(requestMatcher);
        authenticationFilter.setSuccessHandler(successHandler);
        authenticationFilter.setFailureHandler(failureHandler);

        return authenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
