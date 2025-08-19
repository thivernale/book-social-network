package org.thivernale.inventory.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.thivernale.inventory.logging.Logged;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @EventListener(VisitEvent.class)
    @Logged
    public void consumeEvent(VisitEvent event) {
        //
    }
}
