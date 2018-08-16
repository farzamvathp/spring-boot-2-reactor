package com.emersun.imi.security;

import com.emersun.imi.repositories.UserAccountRepository;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtTokenProvider jwtTokenProvider;
    private UserAccountRepository userAccountRepository;

    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider, UserAccountRepository userAccountRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider,userAccountRepository);
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
