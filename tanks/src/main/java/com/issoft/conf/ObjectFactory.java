package com.issoft.conf;

import com.issoft.conf.actor.DynamicObject;

@FunctionalInterface
public interface ObjectFactory {

    DynamicObject create(Location location, char symbol);
}
