package com.ryan.temporarycyclechange.security;

import com.ryan.temporarycyclechange.security.userdetails.User;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rsapl00
 */
@Component
public class Ps01CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        Ps01CustomAuthenticationToken token = (Ps01CustomAuthenticationToken) authentication;

        User user = (User) token.getPrincipal();

        if (!(token.isAuthenticated())) {
            throw new AccessDeniedException("Access Denied for " + user.getName());
        }

        return user;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Ps01CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }

}