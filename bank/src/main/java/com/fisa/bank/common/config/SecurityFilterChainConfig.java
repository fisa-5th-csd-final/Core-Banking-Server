package com.fisa.bank.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    @Bean
    @Order(1)
    // Authorization Server 필터 체인 설정
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                // SAS 엔드포인트만 매칭
                .securityMatcher(authorizationServer.getEndpointsMatcher())
                // SAS 기능 활성화(OIDC 포함)
                .with(authorizationServer, as -> as.oidc(Customizer.withDefaults()))
                .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServer.getEndpointsMatcher()))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // 인증 안 된 HTML 요청은 /login으로
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ))
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(2)
    // 인증이 필요하지 않은 엔드포인트
    public SecurityFilterChain nonAuthenticated(HttpSecurity http) throws Exception {
        commonConfiguration(http);

        http.securityMatcher("/api/users");
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(
                        HttpMethod.POST, "/api/users").permitAll());

        return http.build();
    }

    // FilterChain 공통 설정
    private void commonConfiguration(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
//        http.formLogin(AbstractHttpConfigurer::disable);
    }

}
