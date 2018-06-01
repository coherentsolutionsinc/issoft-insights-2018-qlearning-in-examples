package com.issoft.conf.model;

import com.issoft.conf.GameController;
import com.issoft.conf.GameMatrix;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionExplode;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Shell;
import com.issoft.conf.vision.Vision;

import static com.issoft.conf.action.ActionMove.directionAction;

public class ShellDecisionMaker implements DecisionMaker<Shell> {

    private GameController gameController;

    public ShellDecisionMaker(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void update(Agent agent, Action action, GameMatrix gameMatrix) {
    }

    @Override
    public void update(Vision vision, boolean killed) {
    }

    @Override
    public Action decision(Shell shell) {
        ActionMove action = directionAction(shell.getDirection());
        if (gameController.validAction(shell, action)) {
            return action;
        }
        return new ActionExplode();
    }

}
