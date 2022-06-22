package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.security.jwt.JWTRequestFilter;
import com.github.ivanfomkin.bookshop.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTRequestFilter filter;
    private final JwtBlacklistService jwtBlacklistService;
    private final BookStoreUserDetailsService bookStoreUserDetailsService;
    private static final String LOGIN_URL = "/signin";
    private static final String LOGOUT_URL = "/logout";
    private static final String COOKIE_TOKEN_NAME = "token";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookStoreUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile", "/myarchive")
                .authenticated()
//                .hasRole("USER")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage(LOGIN_URL).failureForwardUrl(LOGIN_URL)
                .and().logout()
                .logoutUrl(LOGOUT_URL)
                .addLogoutHandler(((request, response, authentication) -> {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        Optional<Cookie> tokenCookie = Arrays.stream(cookies).filter(c -> c.getName().equals(COOKIE_TOKEN_NAME)).findFirst();
                        tokenCookie.ifPresent(cookie -> jwtBlacklistService.storeInBlacklist(cookie.getValue()));
                    }
                }))
                .logoutSuccessUrl(LOGIN_URL).deleteCookies(COOKIE_TOKEN_NAME)
                .and().oauth2Login()
                .and().oauth2Client();

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
