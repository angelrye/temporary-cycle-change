package com.ryan.temporarycyclechange;

import com.ryan.temporarycyclechange.auditing.AuditorAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 
 * @author rsapl00
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Ps01JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
    
}