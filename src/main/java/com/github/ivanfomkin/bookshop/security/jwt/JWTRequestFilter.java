package com.github.ivanfomkin.bookshop.security.jwt;

import com.github.ivanfomkin.bookshop.exception.JwtInBlackListException;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetailsService;
import com.github.ivanfomkin.bookshop.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {
    private final JwtBlacklistService jwtBlacklistService;
    private final BookStoreUserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> tokenCookie = Arrays.stream(cookies).filter(c -> c.getName().equals("token")).findFirst();
                if (tokenCookie.isPresent()) {
                    String token = tokenCookie.get().getValue();
                    String username = jwtUtil.extractUsername(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        authenticateUser(request, token, username);
                    }
                }
            }
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, String token, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(token, userDetails)) {
            if (!jwtBlacklistService.existInBlacklist(token)) {
                UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
            } else {
                throw new JwtInBlackListException("Current token in blacklist");
            }
        }
    }
}
