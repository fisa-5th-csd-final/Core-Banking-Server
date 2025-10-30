package com.fisa.bank.common.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
public class SecurityFilterChainConfig {

    @Bean
    @Order(1)
    // Authorization Server 필터 체인 설정
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      @Qualifier("OidcJwtGenerator") OAuth2TokenGenerator<?> tokenGenerator,
                                                                      @Qualifier("OidcJwtDecoder")JwtDecoder jwtDecoder) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        commonConfiguration(http); // 공통 설정
        // SAS 엔드포인트만 매칭

        http.securityMatcher(authorizationServer.getEndpointsMatcher())
            .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll());

        // SAS 기능 활성화(OIDC 포함)
        http.with(authorizationServer, as ->
                            as.tokenGenerator(tokenGenerator)
                                .oidc(
                                        oidc -> oidc
                                            .clientRegistrationEndpoint(Customizer.withDefaults())
                                            .userInfoEndpoint(Customizer.withDefaults())
                                ))
                // 인증 안 된 HTML 요청은 /login으로
            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ));

        http.oauth2ResourceServer(oauth2->
                    oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)
            ));

        return http.build();
    }

    @Bean
    @Order(2)
    // [일반 사용자용] 인증이 필요하지 않은 엔드포인트
    public SecurityFilterChain unAuthenticated(HttpSecurity http, @Qualifier("unAuthenticatedFilter") AuthenticationFilter authenticationFilter) throws Exception {
        commonConfiguration(http);

        http.securityMatcher("/api/users", "/api/login");
        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll());
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(3)
    // [일반 사용자용] 인증이 필요한 엔드포인트 시큐리티 필터체인
    public SecurityFilterChain authenticated(HttpSecurity http, @Qualifier("authenticatedFilter") AuthenticationFilter authenticationFilter) throws Exception{
        commonConfiguration(http);

        http.securityMatcher("/**");
        http.authorizeHttpRequests( auth -> auth.anyRequest().authenticated());
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // FilterChain 공통 설정
    private void commonConfiguration(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 비활성화
    }

}
