package com.issoft.conf.view;

import com.issoft.conf.Events;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Eagle;
import com.issoft.conf.actor.Shell;
import com.issoft.conf.actor.Wall;
import javafx.stage.Stage;

public interface GameView {

    void init(Events events);

    void start(Stage stage);

    void update(Agent agent);

    void update(Shell shell);

    void delete(Agent agent);

    void delete(Shell shell);

    void explode(Shell shell);

    void drawEagle(Eagle eagle);

    void drawWall(Wall wall);

    void drawAgent(Agent agent);

    void drawShell(Shell shell);

    boolean training();
}
