package com.ryan.temporarycyclechange.auditing;

import java.util.Optional;

import com.ryan.temporarycyclechange.security.userdetails.User;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is used for Auditing database entries.
 * 
 * Automatic value insert for Last Update Date, Last Update User,
 * Creation Date, Last Modified Date, 
 * 
 * @author rsapl00
 */
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }
}