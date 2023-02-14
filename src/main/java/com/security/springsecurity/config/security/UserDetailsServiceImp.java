package com.security.springsecurity.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.security.springsecurity.entity.Account;
import com.security.springsecurity.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if(account != null && account.isStatus()){
          return UserDetailsImp.buildUserDetail(account);
        }
        return null;
    }
    
}
