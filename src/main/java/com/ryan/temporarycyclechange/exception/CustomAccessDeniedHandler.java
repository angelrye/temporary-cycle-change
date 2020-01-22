package com.ryan.temporarycyclechange.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rsapl00
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static Log logger = LogFactory.getLog(CustomAccessDeniedHandler.class);

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException ex) throws IOException, ServletException {

        if (request.getRequestURI().indexOf("/rest/") <= 0) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
        } else {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            try {
                response.getWriter()
                        .write(new JSONObject().put("timestamp", LocalDateTime.now()).put("message", "Access denied")
                                .put("path", request.getRequestURI()).put("details", "Access denied").toString());
            } catch (JSONException e1) {
                logger.error("Error encountered while writing the response.", e1);
            }
        }
    }

}