package com.emersun.imi.security;

import com.emersun.imi.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Value("${api.base.url}")
    private String baseUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class).csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers(PathBuilder.build()
                        .setBaseUrl(baseUrl)
                        .addMatcher("/admin/**")
                        .getProtectedURLs()).fullyAuthenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider,userAccountRepository));
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
