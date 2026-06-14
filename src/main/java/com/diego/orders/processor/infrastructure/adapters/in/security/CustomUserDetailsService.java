package com.diego.orders.processor.infrastructure.adapters.in.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return User.builder()
                .username("test")
                .password("$2a$12$sQMOsTfoJHSe70zk/ufOUeQZnIKaUWzCS4fSU94FBxM3Pcy8YjM2O")
                .build();
    }
}