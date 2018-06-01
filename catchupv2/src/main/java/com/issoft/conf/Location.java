package com.issoft.conf;

import java.util.Objects;

public class Location {

    private int x;

    private int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x &&
                y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public void apply(Action action) {
        this.x += action.getDx();
        this.y += action.getDy();
    }

    public void reset(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Location adjust(int dx, int dy) {
        return new Location(getX() + dx, getY() + dy);
    }

    public Location adjust(Action action) {
        return new Location(getX() + action.getDx(), getY() + action.getDy());
    }
}
