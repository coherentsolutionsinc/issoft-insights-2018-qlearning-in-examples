package com.issoft.conf.action;

import com.issoft.conf.GameController;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Direction;
import com.issoft.conf.actor.Shell;

import static com.issoft.conf.actor.Direction.*;

public class ActionMove extends Action {

    public ActionMove(int dx, int dy) {
        super(dx, dy);
    }

    public static ActionMove directionAction(Direction direction) {
        if (direction == LEFT) {
            return left();
        } else if (direction == RIGHT) {
            return right();
        } else if (direction == UP) {
            return up();
        } else if (direction == DOWN) {
            return down();
        }
        throw new IllegalStateException("Invalid direction");
    }

    public static ActionMove stay() {
        return new ActionMove(0, 0);
    }

    public static ActionMove left() {
        return new ActionMove(0, -1);
    }

    public static ActionMove right() {
        return new ActionMove(0, 1);
    }

    public static ActionMove up() {
        return new ActionMove(-1, 0);
    }

    public static ActionMove down() {
        return new ActionMove(1, 0);
    }

    public ActionMove reverse() {
        return new ActionMove(-getDx(), -getDy());
    }

    @Override
    public void apply(GameController gameController, Agent agent) {
        gameController.move(this, agent);
    }

    @Override
    public void apply(GameController gameController, Shell shell) {
        gameController.move(this, shell);
    }

    @Override
    public String toString() {
        return "ActionMove{} " + super.toString();
    }
}
