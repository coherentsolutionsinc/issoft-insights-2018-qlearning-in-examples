package com.issoft.conf;

import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.DynamicObject;
import com.issoft.conf.actor.Shell;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.issoft.conf.constants.Constants.DIM;

public class GameMatrixImpl implements GameMatrix {

    private DynamicObject[][] map = new DynamicObject[DIM][DIM];

    private Map<String, Shell> shellsMap = new HashMap<>();

    private List<String> lines;

    public GameMatrixImpl(String mapName) {
        InputStream inputStream = FXGame.class.getClassLoader().getResourceAsStream(mapName);
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
            lines = stream.collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Exception reading map", e);
        }
    }

    @Override
    public void loadMap(ObjectFactory objectFactory) {
        IntStream.range(0, DIM).forEach(i -> {
            IntStream.range(0, DIM).forEach(j -> {
                map[i][j] = objectFactory.create(new Location(i, j), lines.get(i).charAt(j));
            });
        });
    }

    @Override
    public boolean validAction(DynamicObject dynamicObject, Action action) {
        return isWithinPlayground(dynamicObject, action) && isValidAction(dynamicObject, action);
    }

    @Override
    public void locate(Agent agent) {
        map[agent.getLocation().getX()][agent.getLocation().getY()] = agent;
    }

    @Override
    public void delete(Agent agent) {
        map[agent.getLocation().getX()][agent.getLocation().getY()] = null;
    }

    @Override
    public boolean isValid(Shell shell, Action action) {
        DynamicObject potential = getValue(shell.getLocation().apply(action));
        return potential == null || potential.killable();
    }

    @Override
    public boolean isValid(Agent agent, Action action) {
        DynamicObject potential = getValue(agent.getLocation().apply(action));
        return potential == null || potential.visitable();
    }

    @Override
    public void validateMatrix() {
        AtomicInteger counter = new AtomicInteger();
        IntStream.range(0, DIM).forEach(i -> {
            IntStream.range(0, DIM).forEach(j -> {
                DynamicObject dynamicObject = getValue(i, j);
                if (dynamicObject instanceof Agent) {
                    counter.incrementAndGet();
                }
            });
        });
    }

    @Override
    public void show() {
        IntStream.range(0, DIM).forEach(i -> {
            IntStream.range(0, DIM).forEach(j -> {
                DynamicObject dynamicObject = map[i][j];
                System.out.print(dynamicObject == null ? '0' : dynamicObject.getSymbol());
            });
            System.out.println();
        });
        System.out.println();
    }

    @Override
    public Shell getShell(Location location) {
        for (Shell shell : shellsMap.values()) {
            if (shell.getLocation().equals(location)) {
                return shell;
            }
        }
        return null;
    }

    @Override
    public void putShell(Shell shell) {
        shellsMap.put(shell.getId(), shell);
    }

    @Override
    public void removeShell(Shell shell) {
        shellsMap.remove(shell.getId());
    }

    @Override
    public Map<String, Shell> getShellsMap() {
        return shellsMap;
    }

    @Override
    public DynamicObject getObject(Location location) {
        return map[location.getX()][location.getY()];
    }

    private boolean isValidAction(DynamicObject dynamicObject, Action action) {
        return dynamicObject.isValidAction(action, this);
    }

    private boolean isWithinPlayground(DynamicObject dynamicObject, Action action) {
        return dynamicObject.getLocation().getX() + action.getDx() >= 0 &&
                dynamicObject.getLocation().getX() + action.getDx() < DIM &&
                dynamicObject.getLocation().getY() + action.getDy() >= 0 &&
                dynamicObject.getLocation().getY() + action.getDy() < DIM;
    }

    public boolean available(Agent agent, ActionMove action) {
        return !(map[agent.getLocation().getX() + action.getDx()][agent.getLocation().getY() + action.getDy()] instanceof Agent);
    }

    private DynamicObject getValue(int i, int j) {
        return map[i][j];
    }

    private DynamicObject getValue(Location location) {
        return getValue(location.getX(), location.getY());
    }


}
