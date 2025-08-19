package org.thivernale.inventory.events;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
public class VisitEvent extends ApplicationEvent {
    private final String message;

    public VisitEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
