package org.thivernale.inventory.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.thivernale.inventory.logging.Logged;

@Component
public class VisitorEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @Logged
    public void publishVisitEvent(String message) {
        eventPublisher.publishEvent(new VisitEvent(this, message));
    }
}
