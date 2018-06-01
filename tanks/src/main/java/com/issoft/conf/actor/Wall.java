package com.issoft.conf.actor;

import com.issoft.conf.vision.Vision;
import com.issoft.conf.Location;

public class Wall extends DynamicObject {

    public static final char SYMBOL = '#';

    public Wall(Location location) {
        super(location);
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

    @Override
    public boolean vision(Vision vision) {
        return vision.accept(this);
    }

    @Override
    public boolean visitable() {
        return false;
    }

    @Override
    public boolean killable() {
        return false;
    }

    @Override
    public String toString() {
        return "Wall{} " + super.toString();
    }
}
