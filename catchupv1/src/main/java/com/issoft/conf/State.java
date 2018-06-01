package com.issoft.conf;

import java.util.Objects;

public class State implements Comparable<State> {

    private double distance;

    public State(Location location1, Location location2) {
        this(location1.getX(), location1.getY(), location2.getX(), location2.getY());
    }

    private State(int x1, int y1, int x2, int y2) {
        this.distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return distance == state.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public String toString() {
        return "State{" +
                "distance=" + distance +
                '}';
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(State state) {
        return Double.compare(distance, state.getDistance());
    }
}
