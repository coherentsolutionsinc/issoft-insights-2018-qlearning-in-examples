package com.issoft.conf;

import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionExplode;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.action.ActionShoot;
import com.issoft.conf.actor.*;
import com.issoft.conf.model.PlayerDecisionMaker;
import com.issoft.conf.model.ShellDecisionMaker;
import com.issoft.conf.model.SmartDecisionMaker;
import com.issoft.conf.view.GameView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.issoft.conf.constants.Constants.MODEL_FILE;

public class GameControllerImpl implements GameController, Events {

    private SmartDecisionMaker smartDecisionMaker;
    private ShellDecisionMaker shellDecisionMaker;
    private PlayerDecisionMaker playerDecisionMaker = new PlayerDecisionMaker();

    private GameView gameView;

    private GameMatrix gameMatrix;

    private Map<String, Agent> waitingAgents = new ConcurrentHashMap<>();

    public GameControllerImpl(GameView gameView, GameMatrix gameMatrix) {
        this.gameView = gameView;
        this.gameMatrix = gameMatrix;
        this.smartDecisionMaker = new SmartDecisionMaker(gameMatrix, gameView.training());
        this.shellDecisionMaker = new ShellDecisionMaker(this);
        gameView.init(this);
    }

    @Override
    public void start(Stage primaryStage, int modelNumber) {
        if (!gameView.training()) {
            smartDecisionMaker.loadModel(Paths.get(String.format(MODEL_FILE, modelNumber)));
        }
        gameView.start(primaryStage);
        gameMatrix.loadMap(this::objectFactory);
    }

    @Override
    public synchronized void step(Agent agent) {
        if (!agent.isKilled()) {
            gameMatrix.delete(agent);
            agent.step().apply(this, agent);
            agent.update(gameMatrix);
            gameMatrix.locate(agent);
            gameView.update(agent);
            gameMatrix.validateMatrix();
        }
        waitingAgents.values().forEach(waitingAgent -> waitingAgent.resetAgent(this));
    }

    @Override
    public synchronized void step(Shell shell) {
        if (!shell.isExploded()) {
            checkSpot(shell);
            if (!shell.isExploded()) {
                shell.step().apply(this, shell);
                gameView.update(shell);
            }
        }
    }

    @Override
    public void move(ActionMove actionMove, Agent agent) {
        if (gameMatrix.validAction(agent, actionMove)) {
            agent.apply(actionMove);
        }
    }

    @Override
    public void move(ActionMove actionMove, Shell shell) {
        if (gameMatrix.validAction(shell, actionMove)) {
            shell.apply(actionMove);
        }
        checkSpot(shell);
    }

    @Override
    public void shoot(ActionShoot actionShoot, Agent agent) {
        if (!agent.isReloading()) {
            agent.shoot(actionShoot);
            shoot(agent, actionShoot);
        }
    }

    @Override
    public void explode(ActionExplode actionExplode, Shell shell) {
        destroy(shell);
        explodeShell(shell);
    }

    @Override
    public void respawn(Agent agent) {
        waitingAgents.remove(agent.getId());
        gameMatrix.locate(agent);
        gameView.drawAgent(agent);
        gameView.update(agent);
    }

    private void explodeShell(Shell shell) {
        gameMatrix.removeShell(shell);
        gameView.explode(shell); // animation
        shell.delete(this);
    }

    private void destroy(Shell shell) {
        DynamicObject dynamicObject = gameMatrix.getObject(shell.getLocation());
        if (dynamicObject != null) {
            dynamicObject.destroyed(shell, agent -> {
                agent.delete(this);
            });
            dynamicObject.delete(this);
        }
    }

    private void shoot(Agent agent, ActionShoot actionShoot) {
        Shell shell = new Shell(agent, actionShoot, shellDecisionMaker);
        gameMatrix.putShell(shell);
        gameView.drawShell(shell);
        gameView.update(shell);
    }

    @Override
    public void show() {
        gameMatrix.show();
    }

    @Override
    public void model() {
        smartDecisionMaker.info();
        //smartDecisionMaker.saveModel(MODEL_FILE);
    }

    @Override
    public void nextStep(DynamicObject dynamicObject) {
        dynamicObject.step(this);
        //show();
    }

    @Override
    public void keyPress(KeyCode keyCode) {
        playerDecisionMaker.next(keyCode);
    }

    @Override
    public boolean validAction(DynamicObject dynamicObject, Action action) {
        return gameMatrix.validAction(dynamicObject, action);
    }

    private void checkSpot(Shell shell) {
        DynamicObject dynamicObject = gameMatrix.getObject(shell.getLocation());
        if (dynamicObject != null && !shell.belongsTo(dynamicObject)) {
            new ActionExplode().apply(this, shell);
        }
    }

    @Override
    public void deleted(Agent agent) {
        gameMatrix.delete(agent);
        gameView.delete(agent);
        waitingAgents.put(agent.getId(), agent);
    }

    @Override
    public void deleted(Eagle eagle) {
    }

    private DynamicObject objectFactory(Location location, char symbol) {
        if (symbol == Eagle.SYMBOL) {
            Eagle eagle = new Eagle(location);
            gameView.drawEagle(eagle);
            return eagle;
        } else if (symbol == Wall.SYMBOL) {
            Wall wall = new Wall(location);
            gameView.drawWall(wall);
            return wall;
        } else if (symbol == Agent.SYMBOL) {
            Agent agent = new Agent(location, smartDecisionMaker);
            gameView.drawAgent(agent);
            gameView.update(agent);
            return agent;
        } else if (symbol == 'P') {
            Agent agent = new Agent(location, playerDecisionMaker);
            gameView.drawAgent(agent);
            gameView.update(agent);
            return agent;
        } else if (symbol == '0') {
            return null;
        }
        throw new IllegalStateException("Invalid symbol: " + symbol);
    }

}
