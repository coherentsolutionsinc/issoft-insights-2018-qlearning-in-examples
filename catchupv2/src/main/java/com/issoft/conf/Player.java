package com.issoft.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Player {

    private Location location;

    private List<Action> actions;

    public Player(int x, int y, int speed) {
        this.location = new Location(x, y);
        this.actions = new ArrayList<>();

        for (int i = -speed; i <= speed; i++) {
            for (int j = -speed; j <= speed; j++) {
                actions.add(new Action(i, j));
            }
        }
    }

    public Location getLocation() {
        return location;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void step(Supplier<Action> actionProvider) {
        doAction(actionProvider.get());
    }

    private void doAction(Action action) {
        getLocation().apply(action);
    }

}
