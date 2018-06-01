package com.issoft.conf;

import com.issoft.conf.action.Action;
import com.issoft.conf.action.ActionExplode;
import com.issoft.conf.action.ActionMove;
import com.issoft.conf.action.ActionShoot;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.DynamicObject;
import com.issoft.conf.actor.Eagle;
import com.issoft.conf.actor.Shell;
import javafx.stage.Stage;

public interface GameController {

    void start(Stage primaryStage, int modelNumber);

    void deleted(Agent agent);

    void deleted(Eagle eagle);

    void explode(ActionExplode actionExplode, Shell shell);

    void shoot(ActionShoot actionShoot, Agent agent);

    void move(ActionMove actionMove, Agent agent);

    void move(ActionMove actionMove, Shell agent);

    void step(Agent agent);

    void step(Shell shell);

    boolean validAction(DynamicObject dynamicObject, Action action);

    void respawn(Agent agent);

}
