package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class BookStoreUserDetails implements UserDetails {
    private final List<? extends GrantedAuthority> authorities;
    private final String password;
    private final String username;

    public BookStoreUserDetails(UserEntity user) {
        this.authorities = user.getRoles();
        this.password = user.getPassword();
        this.username = user.getEmail().getContact();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
