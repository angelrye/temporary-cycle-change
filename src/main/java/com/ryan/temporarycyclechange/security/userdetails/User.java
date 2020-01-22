package com.ryan.temporarycyclechange.security.userdetails;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.NonNull;

/**
 * 
 * @author rsapl00
 */
@Data
public class User implements Authentication, Serializable {

    private static final String EMAIL_DOMAIN = "@gmail.com";

    /**
     *
     */
    private static final long serialVersionUID = 7358339418644588868L;

    public User() {
    }

    public User(String username, String division, String group, String fullname) {
        
        this.username = username;
        this.email = username + EMAIL_DOMAIN;
        this.division = division;
        this.groups = group;
        this.fullname = fullname;
        
        if (username == null || "".equals(username) || division == null || "".equals(division)) {
            this.roles = Arrays.asList(RoleType.USER_ANONYMOUS);
        } else {
            this.roles = RoleType.getRoles(group);
        }

    }

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String division;

    @NonNull
    private List<RoleType> roles;

    @NonNull
    private String groups;

    @NonNull
    private String fullname;

    @Override
    public String getName() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return RoleType.getGrantedAuthorities(roles);
    }

    /**
     * @return {@link User}
     */
    @Override
    @JsonIgnore
    public Object getCredentials() {
        return this;
    }

    /**
     * @return the user's email address
     */
    @Override
    public Object getDetails() {
        return this.email;
    }

    /**
     * @return {@link User}
     */
    @Override
    @JsonIgnore
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return isUserValid();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    public Boolean isUserValid() {
        if (username == null || "".equals(username) || division == null || "".equals(division)
                || roles.contains(RoleType.USER_ANONYMOUS) || roles.isEmpty()) {
            return false;
        }

        return true;
    }

    public Boolean isAdmin() {
        if (roles.contains(RoleType.USER_ADMIN)) {
            return true;
        }

        return false;
    }
}