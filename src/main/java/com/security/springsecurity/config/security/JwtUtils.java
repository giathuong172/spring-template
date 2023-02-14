package com.security.springsecurity.config.security;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.security.springsecurity.entity.Account;
import com.security.springsecurity.repository.AccountRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils implements Serializable {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    private final AccountRepository accountRepository;

    private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
    public <T> T getClaimFromToken(String token , Function<Claims,T> claimsResolver){
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public String  getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationFromToken(token);
		return expiration.before(new Date());
	}
    public boolean validateToken(String token, Account account) {
		final String username = getUsernameFromToken(token);
		return (username.equals(account.getUsername()) && account.isStatus() && !isTokenExpired(token));
	}
    public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
    public String generateToken(Authentication authen){
        Date expireDate = new Date(System.currentTimeMillis() + expiration * 1000);
        UserDetailsImp userDetailsImp = (UserDetailsImp) authen.getPrincipal();
        return Jwts.builder().setSubject(userDetailsImp.getUsername())
                             .setIssuedAt(new Date(System.currentTimeMillis()))
                             .setExpiration(expireDate)
                             .signWith(SignatureAlgorithm.HS512, secret)
                             .compact();

    }
    public String generateToken(String username){
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + expiration * 1000);
        return Jwts.builder().setSubject(username)
                             .setIssuedAt(now)
                             .setExpiration(expireDate)
                             .signWith(SignatureAlgorithm.HS512, secret)
                             .compact();

    }
    public Account getCurrentAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			Account account = accountRepository.findByUsername(username);
			return account;
		}
		return null;
	}

}
