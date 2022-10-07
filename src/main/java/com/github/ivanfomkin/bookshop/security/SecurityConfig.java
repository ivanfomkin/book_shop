package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.security.jwt.JWTRequestFilter;
import com.github.ivanfomkin.bookshop.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTRequestFilter filter;
    private final JwtBlacklistService jwtBlacklistService;
    private static final String LOGIN_URL = "/signin";
    private static final String LOGOUT_URL = "/logout";
    private static final String COOKIE_TOKEN_NAME = "token";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(a -> a.antMatchers("/my", "/profile", "/myarchive", "/payment", "/myviewed").authenticated()
                        .antMatchers("/admin/*").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(form -> form.loginPage(LOGIN_URL)
                        .failureForwardUrl(LOGIN_URL))
                .logout(l -> l.logoutUrl(LOGOUT_URL)
                        .addLogoutHandler(((request, response, authentication) -> {
                            Cookie[] cookies = request.getCookies();
                            if (cookies != null) {
                                Optional<Cookie> tokenCookie = Arrays.stream(cookies).filter(c -> c.getName().equals(COOKIE_TOKEN_NAME)).findFirst();
                                tokenCookie.ifPresent(cookie -> jwtBlacklistService.storeInBlacklist(cookie.getValue()));
                            }
                        }))
                        .logoutSuccessUrl(LOGIN_URL)
                        .deleteCookies(COOKIE_TOKEN_NAME))
                .oauth2Login(SecurityConfigurerAdapter::and)
                .oauth2Client(SecurityConfigurerAdapter::and)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
