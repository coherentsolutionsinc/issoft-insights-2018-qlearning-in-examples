package com.issoft.conf;

public class Action {

    private int dx;

    private int dy;

    public Action(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    @Override
    public String toString() {
        return "Action{" +
                "dx=" + dx +
                ", dy=" + dy +
                '}';
    }
}
