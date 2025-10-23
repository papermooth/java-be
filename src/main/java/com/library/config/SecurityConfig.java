package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（JWT不需要）
            .csrf().disable()
            // 禁用HTTP基本认证
            .httpBasic().disable()
            // 禁用表单登录
            .formLogin().disable()
            // 禁用注销
            .logout().disable()
            // 设置会话策略为无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置请求授权
            .authorizeRequests()
            // 允许所有请求，由JwtInterceptor处理实际的认证
            .anyRequest().permitAll();
        
        return http.build();
    }
}