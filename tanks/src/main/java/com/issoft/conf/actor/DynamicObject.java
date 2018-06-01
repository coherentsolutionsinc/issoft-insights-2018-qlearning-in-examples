package com.issoft.conf.actor;

import com.issoft.conf.GameController;
import com.issoft.conf.GameMatrix;
import com.issoft.conf.Location;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.vision.Vision;

import java.util.function.Consumer;

import static com.issoft.conf.action.ActionMove.*;
import static com.issoft.conf.actor.Direction.*;

public abstract class DynamicObject {

    private static final Direction DEFAULT_DIRECTION = UP;

    private Location location;
    private Location initialLocation;

    private Direction direction;

    private String id;

    public DynamicObject(Location location) {
        this(location, DEFAULT_DIRECTION);
    }

    public DynamicObject(Location location, Direction direction) {
        this.location = location.copy();
        this.direction = direction;
        this.id = location.toString();
        this.initialLocation = location.copy();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Action step() {
        return null;
    }

    public void delete(GameController gameController) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getInitialLocation() {
        return initialLocation;
    }

    public abstract char getSymbol();

    public abstract boolean vision(Vision vision);

    public void apply(ActionMove actionMove) {
        setLocation(getLocation().apply(actionMove));
        updateDirection(actionMove);
    }

    private void updateDirection(ActionMove actionMove) {
        if (actionMove.equals(left())) {
            setDirection(LEFT);
        } else if (actionMove.equals(right())) {
            setDirection(RIGHT);
        } else if (actionMove.equals(up())) {
            setDirection(UP);
        } else if (actionMove.equals(down())) {
            setDirection(DOWN);
        }
    }

    public void destroyed(Shell shell, Consumer<Agent> consumer) {
    }

    public void step(GameController gameController) {
    }

    public boolean visitable() {
        return true;
    }

    public boolean killable() {
        return true;
    }

    @Override
    public String toString() {
        return "DynamicObject{" +
                "location=" + location +
                '}';
    }

    public boolean isValidAction(Action action, GameMatrix gameMatrix) {
        return false;
    }
}
