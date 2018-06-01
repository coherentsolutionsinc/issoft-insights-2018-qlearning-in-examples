package com.issoft.conf.action;

import com.issoft.conf.GameController;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Direction;

public class ActionShoot extends Action {

    private Direction direction;

    private int aim; // 0 - no aim, 1 - eagle, 2 - enemy

    public ActionShoot(Direction direction, int aim) {
        super(0, 0);
        this.direction = direction;
        this.aim = aim;
    }

    public Direction getDirection() {
        return direction;
    }

    public static ActionShoot shoot(Direction direction, int aim) {
        return new ActionShoot(direction, aim);
    }

    @Override
    public int getAim() {
        return aim;
    }

    public void apply(GameController gameController, Agent agent) {
        gameController.shoot(this, agent);
    }

    @Override
    public String toString() {
        return "ActionShoot{" +
                "direction=" + direction +
                ", aim=" + aim +
                "} " + super.toString();
    }
}
