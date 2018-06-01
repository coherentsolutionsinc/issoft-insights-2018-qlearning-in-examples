package com.issoft.conf.view;

import com.issoft.conf.Events;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.actor.Eagle;
import com.issoft.conf.actor.Shell;
import com.issoft.conf.actor.Wall;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.issoft.conf.constants.Constants.TRAINING_LIMIT;

public class ConsoleView implements GameView {

    private Events events;

    private int counter;

    private Executor executor = Executors.newFixedThreadPool(1);

    private Map<String, Agent> agentsMap = new ConcurrentHashMap<>();
    private Map<String, Shell> shellsMap = new ConcurrentHashMap<>();

    private boolean training;

    public ConsoleView(boolean training) {
        this.training = training;
    }

    @Override
    public void init(Events events) {
        this.events = events;
    }

    @Override
    public void start(Stage stage) {
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            trigger();
        });
    }

    private void trigger() {
        while (process()) {
            events.show();
        }
        events.model();
    }

    private boolean process() {
        IntStream.range(0, 3).forEach( i -> {
            shellsMap.forEach((id, shell) -> events.nextStep(shell));
        });
        agentsMap.forEach((id, agent) -> events.nextStep(agent));
        return counter++ < TRAINING_LIMIT;
    }

    @Override
    public void update(Agent agent) {
    }

    @Override
    public void update(Shell shell) {
    }

    @Override
    public void delete(Agent agent) {
        agentsMap.remove(agent.getId());
    }

    @Override
    public void delete(Shell shell) {
        shellsMap.remove(shell.getId());
    }

    @Override
    public void explode(Shell shell) {
        delete(shell);
    }

    @Override
    public void drawEagle(Eagle eagle) {
    }

    @Override
    public void drawWall(Wall wall) {
    }

    @Override
    public void drawAgent(Agent agent) {
        agentsMap.put(agent.getId(), agent);
    }

    @Override
    public void drawShell(Shell shell) {
        shellsMap.put(shell.getId(), shell);
    }

    @Override
    public boolean training() {
        return training;
    }

}
