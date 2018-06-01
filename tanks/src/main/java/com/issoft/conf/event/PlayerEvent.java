package com.issoft.conf.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;

public class PlayerEvent extends Event {

    public static final EventType<PlayerEvent> KEY = new EventType<>(Event.ANY, "KEY");

    private KeyCode keyCode;

    public PlayerEvent(KeyCode keyCode) {
        super(KEY);
        this.keyCode = keyCode;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }
}
