package com.ryan.temporarycyclechange.security;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rsapl00
 */
@Component
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Log logger = LogFactory.getLog(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try {
            response.getWriter().write(
                    new JSONObject().put("timestamp", LocalDateTime.now()).put("message", "Access denied").toString());
        } catch (JSONException e1) {
            logger.error("Error while writing JSON response.", e1);
        }
    }

}