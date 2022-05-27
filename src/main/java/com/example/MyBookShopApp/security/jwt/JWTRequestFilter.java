package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.BookStoreUserDetailsService;
import com.example.MyBookShopApp.service.JwtBlacklistService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class JWTRequestFilter extends OncePerRequestFilter {
    private final JwtBlacklistService jwtBlacklistService;
    private final BookStoreUserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;

    public JWTRequestFilter(JwtBlacklistService jwtBlacklistService, BookStoreUserDetailsService userDetailsService, JWTUtil jwtUtil) {
        this.jwtBlacklistService = jwtBlacklistService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    username = jwtUtil.extractUsername(token);
                }
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails)) {
                        if (!jwtBlacklistService.existInBlacklist(token)) {
                            UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
