package com.issoft.conf;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.issoft.conf.CustomCollectors.RANDOM_ACTION_COLLECTOR;

public class QModel {

    private static final double GAMMA = 0.95;
    private static final double ALPHA = 0.01;
    private static final double EPS = 0.0;

    private boolean training = true;

    private Map<State, Double> q = new TreeMap<>();

    private BiFunction<Player, Action, Boolean> validAction;

    public QModel(BiFunction<Player, Action, Boolean> validAction) {
        this.validAction = validAction;
    }

    protected Action decision(Player agent, Player enemy) {
        if (training && Math.random() < EPS) {
            return randomAction(agent);
        } else {
            return bestAction(agent, enemy);
        }
    }

    public void update(State actionState, Player agent, Player enemy) {
        State currentState = new State(agent.getLocation(), enemy.getLocation());
        double score = getScore(actionState);
        score = score + ALPHA * (getReward(currentState) + GAMMA * bestScore(agent, enemy) - score);
        q.put(actionState, score);
    }


    private Action bestAction(Player agent, Player enemy) {
        return validActions(agent)
                .filter(action -> !agent.getLocation().adjust(action).equals(enemy.getLocation()))
                .max(Comparator.comparingDouble(action -> scoreAction(action, agent, enemy)))
                .orElseThrow(IllegalStateException::new);
    }

    private Action randomAction(Player agent) {
        return validActions(agent).collect(RANDOM_ACTION_COLLECTOR);
    }

    private Stream<Action> validActions(Player player) {
        return player.getActions().stream().filter(action -> validAction.apply(player, action));
    }

    private double scoreAction(Action action, Player agent, Player enemy) {
        return getScore(new State(agent.getLocation().adjust(action), enemy.getLocation()));
    }

    private double getScore(State state) {
        return q.getOrDefault(state, 0d);
    }

    private double getReward(State state) {
        return state.getDistance() == 0 ? -1 : 0;
    }

    public void info() {
        q.keySet().forEach(key -> {
            System.out.println(key.getDistance() + " " + q.get(key));
        });
    }

    private double bestScore(Player agent, Player enemy) {
        return scoreAction(bestAction(agent, enemy), agent, enemy);
    }

    public void setTraining(boolean training) {
        this.training = training;
    }
}
