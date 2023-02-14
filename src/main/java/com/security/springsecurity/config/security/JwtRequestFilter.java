package com.security.springsecurity.config.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.springsecurity.entity.Account;
import com.security.springsecurity.model.error.CustomError;
import com.security.springsecurity.repository.AccountRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImp userDetailsServiceImp;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equalsIgnoreCase("/api/login")) {
            filterChain.doFilter(request, response);
        } else {
            String username = null;
            String accessToken = null;
            String requestTokenRequest = request.getHeader("Authorization");
            if (requestTokenRequest != null && requestTokenRequest.startsWith("Bearer ")) {
                accessToken = requestTokenRequest.substring(7).trim();
                if (jwtUtils.validateToken(accessToken)) {
                    try {
                        username = jwtUtils.getUsernameFromToken(requestTokenRequest);
                    } catch (SignatureException e) {
                        log.error("Invalid jwt signature", e.getMessage());
                    } catch (MalformedJwtException e) {
                        log.error("Invalid JWT Token", e.getMessage());
                    } catch (IllegalArgumentException e) {
                        log.error("Unable to get JWT Token", e.getMessage());
                    } catch (ExpiredJwtException e) {
                        log.error("jwt has expired", e.getMessage());
                        CustomError customError = CustomError.builder().code("token.expired").field("accessToken")
                                .message("JWT Token has expired").build();
                        responseToClient(response, customError, HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    Account account = accountRepository.findByUsername(username);
                    if (account != null) {
                        if (jwtUtils.validateToken(requestTokenRequest, account)) {
                            UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(username);
                            if (userDetails != null) {
                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                        username, null, userDetails.getAuthorities());
                                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                filterChain.doFilter(request, response);
                            }
                        }
                    }

                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void responseToClient(HttpServletResponse response, CustomError customError, int httpStatus)
            throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatus(httpStatus);
        Map<String, CustomError> map = new HashMap<>();
        map.put("error", customError);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        response.getOutputStream().print(mapper.writeValueAsString(map));
        response.flushBuffer();
    }

}
