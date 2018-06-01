package com.issoft.conf;

import com.issoft.conf.action.Action;

import java.util.Objects;

public class Location {

    private int x;

    private int y;

    public Location(Location location) {
        this(location.getX(), location.getY());
    }

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
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Location apply(Action action) {
        return new Location(x + action.getDx(), y + action.getDy());
    }

    public Location copy() {
        return new Location(this);
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

    public int distance(Location location) {
        return Math.abs(getX() - location.getX()) + Math.abs(getY() - location.getY());
    }
}
