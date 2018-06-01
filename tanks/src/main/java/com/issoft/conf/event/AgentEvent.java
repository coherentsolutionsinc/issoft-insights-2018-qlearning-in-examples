package com.issoft.conf.event;

import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.DynamicObject;
import javafx.event.Event;
import javafx.event.EventType;

public class AgentEvent extends Event {

    public static final EventType<AgentEvent> DYNAMIC_OBJECT = new EventType<>(Event.ANY, "DYNAMIC_OBJECT");

    private DynamicObject dynamicObject;

    public AgentEvent(DynamicObject dynamicObject) {
        super(DYNAMIC_OBJECT);
        this.dynamicObject = dynamicObject;
    }

    public AgentEvent(EventType<? extends Event> eventType, Agent agent) {
        super(eventType);
        this.dynamicObject = agent;
    }

    public DynamicObject getDynamicObject() {
        return dynamicObject;
    }
}
