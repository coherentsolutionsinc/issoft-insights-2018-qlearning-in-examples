package com.issoft.conf.action;

import com.issoft.conf.GameController;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Shell;
import com.issoft.conf.vision.Vision;

import java.util.Objects;

public abstract class Action {

    private Vision vision;

    private int dx;

    private int dy;

    public Action(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Vision getVision() {
        return vision;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getAim() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return dx == action.dx &&
                dy == action.dy;
    }

    @Override
    public int hashCode() {

        return Objects.hash(dx, dy);
    }

    @Override
    public String toString() {
        return "Action{" +
                "dx=" + dx +
                ", dy=" + dy +
                '}';
    }

    public void apply(GameController gameController, Agent agent) {
    }

    public void apply(GameController gameController, Shell shell) {
    }

    public Action with(Vision vision) {
        this.vision = vision;
        return this;
    }
}
