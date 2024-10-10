package com.lunch.backend.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class CustomUserDetail implements UserDetails {

    private final Long Id;
    private final String email;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}