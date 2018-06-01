package com.issoft.conf.actor;

import com.issoft.conf.GameController;
import com.issoft.conf.Location;
import com.issoft.conf.vision.Vision;

import java.util.function.Consumer;

public class Eagle extends DynamicObject {

    public static final char SYMBOL = '$';

    public Eagle(Location location) {
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
    public void destroyed(Shell shell, Consumer<Agent> consumer) {
        shell.getAgent().killedEagle(shell);
        consumer.accept(shell.getAgent());
    }

    @Override
    public boolean visitable() {
        return false;
    }

    @Override
    public boolean killable() {
        return true;
    }

    @Override
    public void delete(GameController gameController) {
        gameController.deleted(this);
    }

    @Override
    public String toString() {
        return "Eagle{} " + super.toString();
    }
}
