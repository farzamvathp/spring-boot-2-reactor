package com.emersun.imi.security;

import com.emersun.imi.configs.Constants;
import com.emersun.imi.exceptions.UnauthorizedException;
import com.emersun.imi.repositories.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtTokenFilter extends GenericFilterBean {
    private JwtTokenProvider jwtTokenProvider;
    private UserAccountRepository userAccountRepository;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider,UserAccountRepository userAccountRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAccountRepository = userAccountRepository;
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.retrieveToken((HttpServletRequest)servletRequest);
        try {
            if(token != null && jwtTokenProvider.validateToken(token, Constants.ACCESS_TOKEN_AUDIENCE)) {
                String username = jwtTokenProvider.getUsername(token);
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(username));
            }
        } catch (UnauthorizedException e) {
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.setHeader("Access-Control-Allow-Origin","*");
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
