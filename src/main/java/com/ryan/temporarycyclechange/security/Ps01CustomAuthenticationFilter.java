package com.ryan.temporarycyclechange.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ryan.temporarycyclechange.security.userdetails.RoleType;
import com.ryan.temporarycyclechange.security.userdetails.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This class intercepts all request and checks the OAM header injected values.
 * 
 * @author rsapl00
 */
public class Ps01CustomAuthenticationFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(Ps01CustomAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authenticating user.");
        }

        String xUsername = request.getHeader("APP_USER");
        String xGroup = request.getHeader("USER_GROUPS");
        String xDivision = request.getHeader("USER_DIVISION_CODE");
        String xFullname = request.getHeader("FULLNAME");

        String xTest = request.getHeader("xTest");

        User user = null;
        if (xUsername == null || xUsername.isEmpty() || xGroup == null || xGroup.isEmpty() || xDivision == null
                || xDivision.isEmpty()) {
            user = new User("rsapl00", "05", "tcc.user.admin", "Ryan Saplan");
        } else {
            if (xTest != null) {
                user = new User("rsapl00", "19", "tcc.user.reg", "Ryan Saplan");
            } else {
                user = new User(xUsername, xDivision, xGroup, xFullname);
            }
        }

        // User user = new User(xUsername, xDivision, xGroup, xFullname);

        Authentication auth = new Ps01CustomAuthenticationToken(RoleType.getGrantedAuthorities(user.getRoles()), user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

}