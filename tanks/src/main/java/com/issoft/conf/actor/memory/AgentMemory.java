package com.issoft.conf.actor.memory;

import com.issoft.conf.Location;
import com.issoft.conf.action.Action;
import com.issoft.conf.actor.Agent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static com.issoft.conf.action.ActionMove.*;
import static com.issoft.conf.constants.Constants.DIM;

public class AgentMemory implements Memory {

    private static final Location NON_VISITABLE = new Location(-1, -1);

    private Location[][] map = new Location[DIM][DIM];

    private Agent agent;
    private Location nextLocation;
    private Location eagle;

    public AgentMemory(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void look(Location location, boolean horizontal, boolean visitable) {
        if (visitable) {
            map[location.getX()][location.getY()] = agent.getLocation();
        } else {
            map[location.getX()][location.getY()] = NON_VISITABLE;
        }
    }

    @Override
    public Action next() {
        if (eagle != null && map[eagle.getX()][eagle.getY()] != null) {
            return moveTo(eagle);
        }
        if (nextLocation != null && map[nextLocation.getX()][nextLocation.getY()] != null) {
            nextLocation = null;
        }
        if (nextLocation == null) {
            nextLocation = findClosestAvailableNullLocation();
            if (nextLocation == null) {
                return stay();
            }
        }
        return moveTo(nextLocation);
    }

    @Override
    public void reset() {
        map = new Location[DIM][DIM];
        nextLocation = null;
    }

    @Override
    public void show() {
        IntStream.range(0, DIM).forEach(i -> {
            IntStream.range(0, DIM).forEach(j -> {
                System.out.print(map[i][j] + " ");
            });
            System.out.println();
        });
        System.out.println();
    }

    @Override
    public void setEagleLocation(Location eagleLocation) {
        this.eagle = eagleLocation;
    }

    private Action moveTo(Location location) {
        Location target = around(location);
        if (agent.getLocation().equals(target)) {
            return stay();
        }
        while (!agent.getLocation().equals(parentLocation(target))) {
            target = parentLocation(target);
        }
        return actionToLocation(target);
    }

    private Action actionToLocation(Location target) {
        if (agent.getLocation().getX() == target.getX()) {
            if (agent.getLocation().getY() > target.getY()) {
                return left();
            } else if (agent.getLocation().getY() < target.getY()) {
                return right();
            }
        } else if (agent.getLocation().getY() == target.getY()) {
            if (agent.getLocation().getX() > target.getX()) {
                return up();
            } else if (agent.getLocation().getX() < target.getX()) {
                return down();
            }
        }
        throw new IllegalStateException("Invalid target location");
    }

    private Location parentLocation(Location location) {
        return map[location.getX()][location.getY()];
    }

    private Location around(Location location) {
        int i = location.getX();
        int j = location.getY();
        if (seen(i + 1, j)) {
            return new Location(i + 1, j);
        }
        if (seen(i - 1, j)) {
            return new Location(i - 1, j);
        }
        if (seen(i, j + 1)) {
            return new Location(i, j + 1);
        }
        if (seen(i, j - 1)) {
            return new Location(i, j - 1);
        }
        throw new IllegalStateException("Can't find close location");
    }

    private Location findClosestAvailableNullLocation() {
        List<Location> nullLocations = new ArrayList<>();
        int i = agent.getLocation().getX();
        int j = agent.getLocation().getY();
        int k = 0;
        while (nullLocations.size() == 0 && k < DIM) {
            k++;
            for (int ix = -k; ix <= k; ix++) {
                for (int jx = -k; jx <= k; jx++) {
                    if (ix != 0 && jx != 0) {
                        check(i + ix, j + jx, nullLocations);
                    }
                }
            }
        }
        return nullLocations.stream().min(Comparator.comparingInt(o -> o.distance(agent.getLocation()))).orElse(null);
    }

    private void check(int i, int j, List<Location> list) {
        if (i >= 0 && i < DIM && j >= 0 && j < DIM && map[i][j] == null && connected(i, j)) {
            list.add(new Location(i, j));
        }
    }

    private boolean connected(int i, int j) {
        return seen(i + 1, j) || seen(i - 1, j) || seen(i, j + 1) || seen(i, j - 1);
    }

    private boolean seen(int i, int j) {
        return i >= 0 && i < DIM && j >= 0 && j < DIM && map[i][j] != null && !map[i][j].equals(NON_VISITABLE);
    }

}
