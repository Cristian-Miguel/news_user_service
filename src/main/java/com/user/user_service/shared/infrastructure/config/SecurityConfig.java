package com.user.user_service.shared.infrastructure.config;

import com.user.user_service.role.infrastructure.constant.RoleEnum;
import com.user.user_service.shared.infrastructure.utils.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    authRequest ->
                        authRequest
                            .requestMatchers(HttpMethod.PUT,"/api/user").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/user").hasAnyAuthority(RoleEnum.ADMINISTRATOR.getCode(), RoleEnum.NEWS_ENTERPRICE.getCode())
                            .requestMatchers(HttpMethod.GET,"/api/user/**").permitAll()
                            .requestMatchers(HttpMethod.POST,"/api/user").hasAnyAuthority(RoleEnum.ADMINISTRATOR.getCode(), RoleEnum.NEWS_ENTERPRICE.getCode())
                            .anyRequest().authenticated()
                )
                .sessionManagement(sessionManger -> sessionManger.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
