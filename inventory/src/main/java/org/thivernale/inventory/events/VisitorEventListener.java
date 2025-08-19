package org.thivernale.inventory.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thivernale.inventory.logging.Logged;

@Component
public class VisitorEventListener implements ApplicationListener<VisitEvent> {
    private static final Logger log = LoggerFactory.getLogger(VisitorEventListener.class);

    @Override
    @Logged
    @Async
    public void onApplicationEvent(VisitEvent event) {
        //
    }

    /*@Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }*/
}
