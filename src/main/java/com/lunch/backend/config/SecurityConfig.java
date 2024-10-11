package com.lunch.backend.config;

import com.lunch.backend.filter.JwtAuthenticationFilter;
import com.lunch.backend.service.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOriginPattern("http://localhost:5173"); // 허용할 클라이언트 URL
                    config.addAllowedHeader("*");
                    config.addExposedHeader("*");
                    config.addAllowedMethod("GET");
                    config.addAllowedMethod("POST");
                    config.addAllowedMethod("PUT");
                    config.addAllowedMethod("DELETE");// 모든 HTTP 메서드 허용
                    config.setAllowCredentials(true); // 쿠키 허용
                    return config;
                }));

        http
                .headers(headers -> {
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                });

        http
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
                    request.requestMatchers("/").permitAll();
                    request.requestMatchers("/api/auth/login").permitAll();
                    request.requestMatchers("/api/auth/test").permitAll();
                    request.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2.successHandler(
                        new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                response.sendRedirect("http://localhost:5173/login-success");
                            }
                        }
                ))
                .formLogin(Customizer.withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}