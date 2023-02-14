package com.security.springsecurity.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.security.springsecurity.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
@Data
@AllArgsConstructor
@Getter

public class UserDetailsImp implements UserDetails {
    private String username;
    private String password;
    private boolean status;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserDetailsImp(Account account){
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.status = account.isStatus();
        this.authorities = null;
    }
    public static UserDetailsImp buildUserDetail(Account account){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(account.getRole()));
        UserDetailsImp userDetailsImp = new UserDetailsImp(account.getUsername(), account.getPassword(),account.isStatus(), authorityList);
        return userDetailsImp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
