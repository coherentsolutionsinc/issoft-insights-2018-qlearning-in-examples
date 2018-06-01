package com.issoft.conf.actor;

import com.issoft.conf.GameController;
import com.issoft.conf.GameMatrix;
import com.issoft.conf.Location;
import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionShoot;
import com.issoft.conf.actor.memory.AgentMemory;
import com.issoft.conf.actor.memory.Memory;
import com.issoft.conf.model.DecisionMaker;
import com.issoft.conf.vision.Vision;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.issoft.conf.action.ActionMove.*;
import static com.issoft.conf.constants.Constants.KILLED_WAITING;

public class Agent extends DynamicObject {

    public static final char SYMBOL = '@';

    private Memory memory;

    private DecisionMaker<Agent> decisionMaker;

    private int shoots = 0;

    private boolean killed = false;
    private int waiting = 0;

    private Action lastAction;

    private boolean reloading = false;

    public Agent(DecisionMaker<Agent> decisionMaker) {
        this(new Location(0, 0), decisionMaker);
    }

    public Agent(Location location, DecisionMaker<Agent> decisionMaker) {
        super(location);
        this.decisionMaker = decisionMaker;
        this.memory = new AgentMemory(this);
    }

    @Override
    public Action step() {
        //System.out.println("step" + this);
        lastAction = decisionMaker.decision(this);
        return lastAction;
    }

    public void shoot(ActionShoot actionShoot) {
        reloading = true;
        shoots++;
        setDirection(actionShoot.getDirection());
    }

    private Direction defineDirection(Location from, Location to) {
        if (from.getX() == to.getX()) {
            if (from.getY() < to.getY()) {
                return Direction.RIGHT;
            } else if (from.getY() > to.getY()) {
                return Direction.LEFT;
            }
        } else if (from.getY() == to.getY()) {
            if (from.getX() < to.getX()) {
                return Direction.DOWN;
            } else if (from.getX() > to.getX()) {
                return Direction.UP;
            }
        }
        System.out.println(from + " " + to);
        throw new IllegalStateException("Invalid target direction");
    }

    @Override
    public void delete(GameController gameController) {
        killed = true;
        waiting = KILLED_WAITING;
        gameController.deleted(this);
    }

    public void resetAgent(GameController gameController) {
        if (waiting > 0) {
            waiting--;
        } else {
            killed = false;
            setLocation(getInitialLocation());
            memory.reset();
            gameController.respawn(this);
        }
    }

    public List<Action> getActions(Vision vision) {
        List<Action> validActions = new ArrayList<>(List.of(left(), right(), up(), down(), stay()));
        if (vision.getEagle() != null) {
            validActions.add(ActionShoot.shoot(defineDirection(vision.getLocation(), vision.getEagle()), 1));
        }
        vision.getEnemies().forEach(enemy -> {
            validActions.add(ActionShoot.shoot(defineDirection(vision.getLocation(), enemy), 2));
        });
        return validActions;
    }

    @Override
    public String toString() {
        return "Agent{" + getId() + "} " + super.toString();
    }

    public boolean isKilled() {
        return killed;
    }

    public int getShoots() {
        return shoots;
    }

    public void reloaded() {
        reloading = false;
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
        shell.getAgent().killedAgent(shell);
        decisionMaker.update(lastAction.getVision(), true);
    }

    public void killedEagle(Shell shell) {
        decisionMaker.update(shell.getActionShoot().getVision(), false);
    }

    private void killedAgent(Shell shell) {
        decisionMaker.update(shell.getActionShoot().getVision(), false);
    }

    @Override
    public void step(GameController gameController) {
        gameController.step(this);
    }

    @Override
    public boolean visitable() {
        return false;
    }

    @Override
    public boolean killable() {
        return true;
    }

    public boolean isReloading() {
        return reloading;
    }

    public Memory getMemory() {
        return memory;
    }

    @Override
    public boolean isValidAction(Action action, GameMatrix gameMatrix) {
        return gameMatrix.isValid(this, action);
    }

    public void update(GameMatrix gameMatrix) {
        decisionMaker.update(this, lastAction, gameMatrix);
    }
}
