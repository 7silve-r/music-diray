package com.silver.diary.config;

import com.silver.diary.exception.BusinessException;
import com.silver.diary.service.UserService;
import com.silver.diary.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain security(HttpSecurity http, JwtUtil jwt, UserService users,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> { })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error", "/api/reg", "/api/login", "/public/**",
                                "/music/public/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((req, res, ex) -> resolver.resolveException(req, res, null,
                                new BusinessException(401, "请先登录再操作")))
                        .accessDeniedHandler((req, res, ex) -> resolver.resolveException(req, res, null,
                                new BusinessException(403, "没有权限执行此操作"))))
                .addFilterBefore(new JwtFilter(jwt, users, resolver), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
