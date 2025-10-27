package com.fisa.bank.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain nonAuthenticated(HttpSecurity http) throws Exception {
        commonConfiguration(http);

        http.securityMatcher("/api/users");
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(
                        HttpMethod.POST, "/api/users").permitAll());

        return http.build();
    }

    private void commonConfiguration(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
    }

}
