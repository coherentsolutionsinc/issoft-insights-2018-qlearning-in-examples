package com.issoft.conf.action;

import com.issoft.conf.GameController;
import com.issoft.conf.actor.Shell;

public class ActionExplode extends Action {

    public ActionExplode() {
        super(0, 0);
    }

    public void apply(GameController gameController, Shell shell) {
        gameController.explode(this, shell);
    }

    @Override
    public String toString() {
        return "ActionExplode{} " + super.toString();
    }
}
