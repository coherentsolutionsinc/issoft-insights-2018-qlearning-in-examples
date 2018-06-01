package com.issoft.conf.actor.memory;

import com.issoft.conf.Location;
import com.issoft.conf.action.Action;

public interface Memory {

    void look(Location location, boolean horizontal, boolean visitable);

    Action next();

    void reset();

    void show();

    void setEagleLocation(Location eagleLocation);
}
