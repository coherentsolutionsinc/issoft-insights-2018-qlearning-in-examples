package com.issoft.conf;

import com.issoft.conf.action.Action;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.DynamicObject;
import com.issoft.conf.actor.Shell;

import java.util.Map;

public interface GameMatrix {

    void loadMap(ObjectFactory objectFactory);

    boolean validAction(DynamicObject dynamicObject, Action action);

    void locate(Agent agent);

    DynamicObject getObject(Location location);

    void delete(Agent agent);

    boolean isValid(Shell shell, Action action);

    boolean isValid(Agent agent, Action action);

    void validateMatrix();

    void show();

    Shell getShell(Location location);

    void putShell(Shell shell);

    void removeShell(Shell shell);

    Map<String, Shell> getShellsMap();
}
