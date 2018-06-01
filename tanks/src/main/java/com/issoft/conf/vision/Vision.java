package com.issoft.conf.vision;

import com.issoft.conf.GameMatrix;
import com.issoft.conf.Location;
import com.issoft.conf.action.Action;
import com.issoft.conf.actor.*;
import com.issoft.conf.actor.memory.Memory;

import java.util.ArrayList;
import java.util.List;

import static com.issoft.conf.constants.Constants.DIM;

public class Vision {

    private List<Location> enemies = new ArrayList<>();

    private Location eagle;

    private List<Location> shells = new ArrayList<>();

    private Location location;

    private boolean explore = false;

    private int aim = 0;

    private List<String> shellsList;

    private List<String> locationsList = new ArrayList<>();

    public Vision(GameMatrix gameMatrix, Agent agent, Action action) {
        this.location = agent.getLocation().apply(action);
        this.aim = action.getAim();
        mapVision(gameMatrix, null);
        Action exploreAction = agent.getMemory().next();
        if (exploreAction != null) {
            setExplore(exploreAction.getDx() == action.getDx() && exploreAction.getDy() == action.getDy());
        }
    }

    public Vision(GameMatrix gameController, Agent agent) {
        this.location = agent.getLocation().copy();
        this.aim = 0;
        mapVision(gameController, agent.getMemory());
    }

    private void mapVision(GameMatrix gameMatrix, Memory memory) {
        shellsList = new ArrayList<>(gameMatrix.getShellsMap().keySet());
        processShells(gameMatrix, location);
        run(gameMatrix, 0, -1, memory);
        run(gameMatrix, 0, 1, memory);
        run(gameMatrix, -1, 0, memory);
        run(gameMatrix, 1, 0, memory);
    }

    private void run(GameMatrix gameMatrix, int ix, int jx, Memory memory) {
        boolean visible = true;
        int i = location.getX();
        int j = location.getY();
        while (visible) {
            if (j + jx >= 0 && j + jx < DIM && i + ix >= 0 && i + ix < DIM) {
                Location location = new Location(i + ix, j + jx);
                DynamicObject dynamicObject = gameMatrix.getObject(location);
                if (dynamicObject != null) {
                    visible = dynamicObject.vision(this);
                }
                processShells(gameMatrix, location);
                if (memory != null) {
                    memory.look(location, ix == 0, visible);
                }
            } else {
                visible = false;
            }
            j += jx;
            i += ix;
        }
        if (eagle != null && memory != null) {
            memory.setEagleLocation(eagle);
        }
    }

    private void processShells(GameMatrix gameMatrix, Location location) {
        Shell shell = gameMatrix.getShell(location);
        if (shell != null) {
            locationsList.add(location.toString() + shell.getLocation().toString());
            shell.vision(this);
        }
    }

    public List<Location> getEnemies() {
        return enemies;
    }

    public Location getEagle() {
        return eagle;
    }

    public List<Location> getShells() {
        return shells;
    }

    public boolean accept(Wall wall) {
        return false;
    }

    public int getAim() {
        return aim;
    }

    public boolean accept(Shell shell) {
        if (shell.getLocation().equals(location)) {
            shells.add(shell.getLocation().copy());
        } else if (shell.getLocation().getX() == location.getX()) {
            if (shell.getLocation().getY() >= location.getY() && shell.getDirection() == Direction.LEFT) {
                shells.add(shell.getLocation().copy());
            } else if (shell.getLocation().getY() <= location.getY() && shell.getDirection() == Direction.RIGHT) {
                shells.add(shell.getLocation().copy());
            }
        } else if (shell.getLocation().getY() == location.getY()) {
            if (shell.getLocation().getX() >= location.getX() && shell.getDirection() == Direction.UP) {
                shells.add(shell.getLocation().copy());
            } else if (shell.getLocation().getX() <= location.getX() && shell.getDirection() == Direction.DOWN) {
                shells.add(shell.getLocation().copy());
            }
        }
        return true;
    }

    public boolean accept(Eagle eagle) {
        this.eagle = eagle.getLocation().copy();
        return true;
    }

    public boolean accept(Agent agent) {
        enemies.add(agent.getLocation().copy());
        return true;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isExplore() {
        return explore;
    }

    public void setExplore(boolean explore) {
        this.explore = explore;
    }

    @Override
    public String toString() {
        return "Vision{" +
                "enemies=" + enemies +
                ", eagle=" + eagle +
                ", shells=" + shells +
                ", location=" + location +
                ", explore=" + explore +
                ", aim=" + aim +
                '}';
    }

    public List<String> getShellsList() {
        return shellsList;
    }

    public List<String> getLocationsList() {
        return locationsList;
    }
}
