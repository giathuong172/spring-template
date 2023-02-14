package com.security.springsecurity.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;


    @Bean
    public PasswordEncoder passwordEncode(){
        return new BCryptPasswordEncoder(11);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf().disable().cors().and().authorizeHttpRequests()
           .antMatchers("/api/login").permitAll().anyRequest().authenticated();
           return http.build();       
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImp);
        authenticationProvider.setPasswordEncoder(passwordEncode());
        return authenticationProvider;
    }


    
}
