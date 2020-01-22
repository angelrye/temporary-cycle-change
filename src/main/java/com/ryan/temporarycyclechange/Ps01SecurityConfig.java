package com.ryan.temporarycyclechange;

import com.ryan.temporarycyclechange.exception.CustomAccessDeniedHandler;
import com.ryan.temporarycyclechange.security.Ps01CustomAuthenticationFilter;
import com.ryan.temporarycyclechange.security.Ps01CustomAuthenticationProvider;
import com.ryan.temporarycyclechange.security.RestAuthenticationEntryPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * 
 * @author rsapl00
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@ComponentScan("com.ryan.temporarycyclechange.security")
public class Ps01SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Log logger = LogFactory.getLog(Ps01SecurityConfig.class);

    @Autowired
    private Ps01CustomAuthenticationProvider authenticationProvider;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    /** Public URLs. */
    private static final String[] PUBLIC_MATCHERS = {
        "/webjars/**",
        "/css/**",
        "/js/**",
        "/images/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("Configuring HTTP Security.");
        }

        http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyRole("ADMIN")
                .antMatchers("/rest/cyclechanges/approve").hasRole("ADMIN")
                .antMatchers("/rest/cyclechanges/reject").hasRole("ADMIN")
                .antMatchers("/rest/**").hasAnyRole("ADMIN", "REG")
                .antMatchers("/home").hasAnyRole("ADMIN", "REG")
                .antMatchers("/").hasAnyRole("ADMIN", "REG")
                .anyRequest().authenticated()
            .and()
                .addFilterBefore(new Ps01CustomAuthenticationFilter(), BasicAuthenticationFilter.class)
            .formLogin().disable()
            .httpBasic().disable()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .requestMatchers(EndpointRequest.to("info"))
            .antMatchers("/h2-console/**")
            .antMatchers(PUBLIC_MATCHERS)
            .antMatchers("/access-denied");
	}
}