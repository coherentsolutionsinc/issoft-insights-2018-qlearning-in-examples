package com.issoft.conf.model;

import com.issoft.conf.GameMatrix;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.action.ActionShoot;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.vision.Vision;
import javafx.scene.input.KeyCode;

import java.util.function.Function;

import static com.issoft.conf.action.ActionMove.stay;

public class PlayerDecisionMaker implements DecisionMaker<Agent> {

    private Function<Agent, Action> nextAction;

    @Override
    public void update(Agent agent, Action action, GameMatrix gameMatrix) {
    }

    @Override
    public void update(Vision vision, boolean killed) {
    }

    @Override
    public Action decision(Agent agent) {
        if (nextAction != null) {
            Action action = nextAction.apply(agent);
            nextAction = null;
            return action;
        }
        return stay();
    }

    public void next(KeyCode keyCode) {
        switch (keyCode) {
            case SPACE:
                nextAction = this::shoot;
                break;
            case RIGHT:
                nextAction = this::right;
                break;
            case DOWN:
                nextAction = this::down;
                break;
            case LEFT:
                nextAction = this::left;
                break;
            case UP:
                nextAction = this::up;
                break;
        }
    }

    private Action shoot(Agent agent) {
        return new ActionShoot(agent.getDirection(), -2);
    }

    private Action right(Agent agent) {
        return ActionMove.right();
    }

    private Action left(Agent agent) {
        return ActionMove.left();
    }

    private Action up(Agent agent) {
        return ActionMove.up();
    }

    private Action down(Agent agent) {
        return ActionMove.down();
    }
}
