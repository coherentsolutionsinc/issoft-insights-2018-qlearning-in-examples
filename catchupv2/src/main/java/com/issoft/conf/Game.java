package com.issoft.conf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {

    private static final int ENEMY_SPEED = 1;
    private static final int AGENT_SPEED = 2;

    private QModel model;

    private int n;

    private long steps;

    private Player agent;

    private Player enemy;

    public Game(int dimension) {
        this.n = dimension;
        this.model = new QModel(this::validAction);

        this.enemy = new Player(n - 1, n - 1, ENEMY_SPEED);
        this.agent = new Player(0, 0, AGENT_SPEED);
        this.steps = 0;
    }

    private void reset() {
        enemy.getLocation().reset(n - 1, n - 1);
        agent.getLocation().reset(0, 0);
        steps = 0;
    }

    public static void main(String[] args) {
        Game game = new Game(7);
        int rounds = 100;
        List<Long> results = new ArrayList<>();
        boolean smart = false;
        if (args.length > 0) {
            smart = true;
        }
        for (int i = 0; i < rounds; i++) {
            results.add(game.play(false, smart));
            game.reset();
        }
        System.out.println("Max steps: " + results.stream().mapToLong(item -> item).max().getAsLong());
        game.noTraining();
        System.out.println("Result: " + game.play(true, smart));
        game.info();
    }

    private void noTraining() {
        model.setTraining(false);
    }

    private void info() {
        model.info();
    }

    private long play(boolean display, boolean smart) {
        while (!complete() && steps < 100) {
            step(smart);
            if (display) {
                display();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return steps;
    }

    private void display() {
        System.out.print("\033[H\033[2J");
        for (int j = 0; j < n + 2; j++) {
            System.out.print("-");
        }
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.print("|");
            for (int j = 0; j < n; j++) {
                if (agent.getLocation().equals(new Location(i, j))) {
                    System.out.print("@");
                } else if (enemy.getLocation().equals(new Location(i, j))) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.print("|");
            System.out.println();
        }
        for (int j = 0; j < n + 2; j++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println();
    }

    private void step(boolean smart) {
        agent.step(this::smartAction);
        State actionState = new State(agent.getLocation(), enemy.getLocation());
        if (smart) {
            enemy.step(this::targetAction2);
        } else {
            enemy.step(this::targetAction);
        }
        model.update(actionState, agent, enemy);
        steps++;
    }

    private Action smartAction() {
        return model.decision(agent, enemy);
    }

    private Action targetAction() {
        return enemy.getActions().stream()
                .min(Comparator.comparingDouble(action -> new State(enemy.getLocation().adjust(action), agent.getLocation()).getDistance()))
                .orElseThrow(IllegalStateException::new);
    }

    private Action targetAction2() {
        int dx = 0;
        int dy = 0;
        int distance = Math.max(Math.abs(agent.getLocation().getX() - enemy.getLocation().getX()), Math.abs(agent.getLocation().getY() - enemy.getLocation().getY()));
        if (distance == 1) {
            dx = agent.getLocation().getX() - enemy.getLocation().getX();
            dy = agent.getLocation().getY() - enemy.getLocation().getY();
        } else {
            if (agent.getLocation().getX() == enemy.getLocation().getX()) {
                if (agent.getLocation().getX() < n / 2) {
                    dx = 1;
                } else {
                    dx = -1;
                }
            } else if (Math.abs(agent.getLocation().getX() - enemy.getLocation().getX()) > 1) {
                dx = Math.abs(agent.getLocation().getX() - enemy.getLocation().getX()) / (agent.getLocation().getX() - enemy.getLocation().getX());
            }
            if (agent.getLocation().getY() == enemy.getLocation().getY()) {
                if (agent.getLocation().getY() < n / 2) {
                    dy = 1;
                } else {
                    dy = -1;
                }
            } else if (Math.abs(agent.getLocation().getY() - enemy.getLocation().getY()) > 1) {
                dy = Math.abs(agent.getLocation().getY() - enemy.getLocation().getY()) / (agent.getLocation().getY() - enemy.getLocation().getY());
            }
        }
        return new Action(dx, dy);
    }

    private boolean complete() {
        return agent.getLocation().equals(enemy.getLocation());
    }

    private boolean validAction(Player player, Action action) {
        return player.getLocation().getX() + action.getDx() >= 0 &&
                player.getLocation().getX() + action.getDx() < n &&
                player.getLocation().getY() + action.getDy() >= 0 &&
                player.getLocation().getY() + action.getDy() < n;
    }
}
