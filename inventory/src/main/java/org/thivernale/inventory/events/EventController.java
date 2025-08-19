package org.thivernale.inventory.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thivernale.inventory.logging.Logged;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VisitorEventPublisher visitorEventPublisher;

    @GetMapping
    @Logged
    public void fireEvent(@RequestParam(name = "message") String message) {
        applicationEventPublisher.publishEvent(new VisitEvent(this, message));
    }

    @GetMapping("/publish")
    public void fireVisitorEvent(@RequestParam(name = "message") String message) {
        visitorEventPublisher.publishVisitEvent(message);
    }
}
