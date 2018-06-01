package com.issoft.conf.model;

import com.issoft.conf.GameMatrix;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.vision.Vision;

public class FixedActionDecisionMaker implements DecisionMaker<Agent> {

    private ActionMove action;

    public FixedActionDecisionMaker(ActionMove action) {
        this.action = action;
    }

    public Action decision(Vision vision) {
        return action;
    }

    @Override
    public void update(Agent agent, Action action, GameMatrix gameMatrix) {
    }

    @Override
    public void update(Vision vision, boolean killed) {
    }

    @Override
    public Action decision(Agent agent) {
        return null;
    }

}
