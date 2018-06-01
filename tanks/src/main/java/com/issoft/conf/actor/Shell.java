package com.issoft.conf.actor;

import com.issoft.conf.GameController;
import com.issoft.conf.GameMatrix;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionShoot;
import com.issoft.conf.model.DecisionMaker;
import com.issoft.conf.vision.Vision;

public class Shell extends DynamicObject {

    public static final char SYMBOL = '*';

    private boolean exploded = false;

    private Agent agent;

    private ActionShoot actionShoot;

    private DecisionMaker<Shell> decisionMaker;

    public Shell(Agent agent, ActionShoot actionShoot, DecisionMaker<Shell> decisionMaker) {
        super(agent.getLocation(), agent.getDirection());
        setId(agent.getId() + agent.getShoots() + agent.getLocation() + agent.getDirection());
        this.agent = agent;
        this.actionShoot = actionShoot;
        this.decisionMaker = decisionMaker;
    }

    @Override
    public Action step() {
        return decisionMaker.decision(this);
    }

    @Override
    public void delete(GameController gameController) {
        exploded = true;
        agent.reloaded();
    }

    public boolean isExploded() {
        return exploded;
    }

    public boolean belongsTo(DynamicObject dynamicObject) {
        return this.agent == dynamicObject;
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
    public String toString() {
        return "Shell{" + getId() + "} " + super.toString();
    }

    public Agent getAgent() {
        return agent;
    }

    public ActionShoot getActionShoot() {
        return actionShoot;
    }

    @Override
    public void step(GameController gameController) {
        //System.out.println("step" + this);
        gameController.step(this);
    }

    @Override
    public boolean isValidAction(Action action, GameMatrix gameMatrix) {
        return gameMatrix.isValid(this, action);
    }
}
