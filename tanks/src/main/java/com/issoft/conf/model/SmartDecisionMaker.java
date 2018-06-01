package com.issoft.conf.model;

import com.issoft.conf.GameMatrix;
import com.issoft.conf.State;
import com.issoft.conf.action.Action;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.vision.Vision;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.issoft.conf.constants.Constants.TRAINING_LIMIT;
import static com.issoft.conf.util.CustomCollectors.RANDOM_ACTION_COLLECTOR;

public class SmartDecisionMaker implements DecisionMaker<Agent> {

    private static final double GAMMA = 0.5;
    private static final double ALPHA = 0.001;
    private static final double EPS = 0.75;
    private static final String SCORE = "score";

    private Map<State, Double> q = new HashMap<>();

    private long trainingCounter = 0;
    private boolean training;

    private GameMatrix gameMatrix;

    public SmartDecisionMaker(GameMatrix gameMatrix, boolean training) {
        this.gameMatrix = gameMatrix;
        this.training = training;
    }

    @Override
    public Action decision(Agent agent) {
        trainingCounter++;
        return nextAction(agent, gameMatrix);
    }

    @Override
    public synchronized void update(Agent agent, Action action, GameMatrix gameMatrix) {
        //System.out.println("UPDATE");
        if (action == null) return;
        State state = new State(action.getVision());
        double score = getScore(state);
        double reward = getReward(action.getVision());
        //System.out.println(state + " " + score + " " + reward);
        // do not include next step if game is over
        Vision bestVision = bestAction(agent, gameMatrix).getVision();
        double bestScore = scoreAction(bestVision);
        //System.out.println(bestScore + " " + bestVision);
        score = score + ALPHA * (reward + GAMMA * bestScore - score);
        //System.out.println(score + " " + state);
        q.put(state, score);
    }

    @Override
    public synchronized void update(Vision vision, boolean killed) {
        if (vision == null) return;
        State state = new State(vision);
        double score = getScore(state);
        double reward = getReward(vision, killed);
        //if (reward < 0) System.out.println(state);
        //System.out.println(state + " " + score + " " + reward);
        // do not include next step if game is over
        score = score + ALPHA * reward;
        if (state.getShellDistance() == -1 && state.getEnemyDistance() == -1 && reward < 0) {
            System.out.println(score + " " + reward + " " + state + vision + vision.getShellsList() + vision.getLocationsList());
        }
        q.put(state, score);
    }

    public synchronized void info() {
        DecimalFormat df = new DecimalFormat("####0.000000");
        q.keySet().stream().sorted(State::compareTo).forEach((state) -> {
            System.out.println("e:" + state.getEnemyDistance() + " s:" + state.getShellDistance() + " o:" + state.getEagleDistance() + " a:" + state.getAim() + " " + state.isExplore() + " > " + df.format(q.get(state)));
        });
        System.out.println(trainingCounter);
    }

    private Action nextAction(Agent agent, GameMatrix gameMatrix) {
        if (trainingCounter % 100 == 0) {
            System.out.println(trainingCounter + " - " + eps());
        }
        if (training && trainingCounter < TRAINING_LIMIT && Math.random() <= eps()) {
            return randomAction(agent, gameMatrix);
        }
        return bestAction(agent, gameMatrix);
    }

    private double eps() {
        return EPS * (TRAINING_LIMIT - trainingCounter) / TRAINING_LIMIT;
    }

    private Action randomAction(Agent agent, GameMatrix gameMatrix) {
        return validActions(agent, gameMatrix).stream()
                .collect(RANDOM_ACTION_COLLECTOR);
    }

    private Action bestAction(Agent agent, GameMatrix gameMatrix) {
        /*Action action = agent.getMemory().next();
        return action.with(new Vision(gameMatrix, agent, action));*/
        /*return validActions(agent, vision, gameMatrix).stream()
                .max((o1, o2) -> -Double.compare(scoreAction(o1.getVision()), scoreAction(o2.getVision())))
                .orElseThrow(IllegalStateException::new);*/
        Action bestAction = null;
        double bestScore = -100000;
        boolean bestExplore = false;
        for (Action action : validActions(agent, gameMatrix)) {
            double score = scoreAction(action.getVision());
            boolean explore = action.getVision().isExplore();
            //System.out.println(score + " " + action + " " + action.getVision());
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
                bestExplore = explore;
            } else if (score == bestScore && !bestExplore && explore) {
                bestAction = action;
                bestExplore = true;
            }
        }
        //System.out.println("BEST: " + bestScore + " " + bestAction + " " + bestAction.getVision());
        return bestAction;
    }

    private List<Action> validActions(Agent agent, GameMatrix gameMatrix) {
        Vision vision = new Vision(gameMatrix, agent);
        return agent.getActions(vision).stream()
                    .filter(action -> gameMatrix.validAction(agent, action))
                    .map(action -> action.with(new Vision(gameMatrix, agent, action)))
                    .collect(Collectors.toList());
    }

    private double scoreAction(Vision vision) {
        return getScore(new State(vision));
    }

    private double getScore(State state) {
        Double score = q.get(state);
        if (score == null) {
            q.put(state, 0d);
            return 0;
        }
        return score;
    }

    private double getReward(Vision vision, boolean killed) {
        if (killed) {
            return -1;
        } else if (vision.getAim() == 1) {
            return 100;
        } else if (vision.getAim() == 2) {
            return 1;
        }
        return 0;
    }

    private double getReward(Vision vision) {
        if (vision.isExplore()) {
            return 0.01;
        } else {
            return 0;
        }
    }

    public void saveModel(String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            q.keySet().forEach(state -> {
                JSONObject json = state.toJSON();
                json.put(SCORE, q.get(state));
                try {
                    fw.write(json.toString() + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadModel(Path modelPath) {
        try (Stream<String> stream = Files.lines(modelPath)) {
            List<String> lines = stream.collect(Collectors.toList());
            lines.forEach(line -> {
                JSONObject json = new JSONObject(line);
                q.put(new State(json), json.getDouble(SCORE));
            });
        } catch (Exception e) {
            throw new RuntimeException("Exception loading model", e);
        }
        /*q.keySet().stream().forEach(state -> {
            if ((state.getEnemyDistance() == -1 || state.getEnemyDistance() > 3) && (state.getShellDistance() == -1 || state.getShellDistance() > 3)) {
                if (state.isExplore()) {
                    q.put(state, 3d);
                } else {
                    q.put(state, 2d);
                }
            }
        });*/
        //info();
    }

}
