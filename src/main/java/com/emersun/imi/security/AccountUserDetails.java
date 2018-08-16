package com.emersun.imi.security;

import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AccountUserDetails implements UserDetailsService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private Messages messages;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Option.of(userAccountRepository.findByUsername(username)
                .block())
                .map(user -> User.withUsername(user.getUsername())
                        .roles(user.getRole())
                        .password(user.getPassword())
                        .accountLocked(user.getBanned())
                        .build()
                ).getOrElseThrow(() -> new UsernameNotFoundException(messages.get(Response.USERNAME_NOT_FOUND)));
    }
}
