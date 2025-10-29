package com.fisa.bank.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    private final JwtDecoder jwtDecoder;
    private final Converter<Jwt, AbstractAuthenticationToken> authenticationConverter;

    @Bean
    @Order(1)
    // Authorization Server 필터 체인 설정
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        commonConfiguration(http); // 공통 설정
        // SAS 엔드포인트만 매칭

        http.securityMatcher(authorizationServer.getEndpointsMatcher())
            .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll());

        // SAS 기능 활성화(OIDC 포함)
        http.with(authorizationServer, as ->
                            as.oidc(
                                    oidc -> oidc
                                            .clientRegistrationEndpoint(Customizer.withDefaults())
                                            .userInfoEndpoint(Customizer.withDefaults())
                            ))
                // 인증 안 된 HTML 요청은 /login으로
            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ));

        return http.build();
    }

    @Bean
    @Order(2)
    // 인증이 필요하지 않은 엔드포인트
    public SecurityFilterChain nonAuthenticated(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
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
    // 인증이 필요한 엔드포인트 시큐리티 필터체인
    public SecurityFilterChain authenticated(HttpSecurity http) throws Exception{
        commonConfiguration(http);

        http.securityMatcher("/**");
        http.authorizeHttpRequests( auth -> auth.anyRequest().authenticated());

        http.oauth2ResourceServer(oauth ->
                oauth.jwt(jwt -> {
                    jwt.decoder(jwtDecoder);
                    jwt.jwtAuthenticationConverter(authenticationConverter);
                })
        );
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
