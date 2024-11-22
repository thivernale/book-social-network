package org.thivernale.booknetwork.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thivernale.booknetwork.user.User;

import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<Long> {
    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        return Optional.ofNullable(((User) authentication.getPrincipal()).getId());
    }
}
