package com.ryan.temporarycyclechange.security;

import java.util.Collection;

import com.ryan.temporarycyclechange.security.userdetails.User;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author rsapl00
 */
public class Ps01CustomAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -2559357568133352085L;
    private User authenticatedUser;

    @Override
    public Object getCredentials() {
        return authenticatedUser.getUsername();
    }

    /**
     * @return {@link User}
     */
    @Override
    public Object getPrincipal() {
        return authenticatedUser;
    }

    public Ps01CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities, User authenticatedUser) {
        super(authorities);
        this.authenticatedUser = authenticatedUser;
        setAuthenticated(authenticatedUser.isAuthenticated());
    }

}
