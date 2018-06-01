package com.issoft.conf;

import com.issoft.conf.vision.Vision;
import org.json.JSONObject;

import java.util.Objects;

public class State implements Comparable<State> {

    private int enemyDistance = -1;

    private int shellDistance = -1;

    private int eagleDistance = -1;

    private int aim = 0;

    private boolean explore = false;

    public State(Vision vision) {
        if (vision.getEnemies().size() > 0) {
            this.enemyDistance = vision.getEnemies().stream().mapToInt(enemy -> vision.getLocation().distance(enemy)).min().orElseThrow(IllegalStateException::new);
        }
        if (vision.getShells().size() > 0) {
            this.shellDistance = vision.getShells().stream().mapToInt(shell -> vision.getLocation().distance(shell)).min().orElseThrow(IllegalStateException::new);
        }
        if (vision.getEagle() != null) {
            this.eagleDistance = vision.getEagle().distance(vision.getLocation());
        }
        this.aim = vision.getAim();
        this.explore = vision.isExplore();
    }

    public State(JSONObject json) {
        this.enemyDistance = json.getInt("enemyDistance");
        this.shellDistance = json.getInt("shellDistance");
        this.eagleDistance = json.getInt("eagleDistance");
        this.aim = json.getInt("aim");
        this.explore = json.getBoolean("explore");
    }

    public int getEnemyDistance() {
        return enemyDistance;
    }

    public int getShellDistance() {
        return shellDistance;
    }

    public int getEagleDistance() {
        return eagleDistance;
    }

    public int getAim() {
        return aim;
    }

    public boolean isExplore() {
        return explore;
    }

    @Override
    public int compareTo(State state) {
        if (enemyDistance != state.getEnemyDistance()) {
            return Integer.compare(enemyDistance, state.getEnemyDistance());
        } else if (shellDistance != state.getShellDistance()) {
            return Integer.compare(shellDistance, state.getShellDistance());
        } else if (eagleDistance != state.getEagleDistance()) {
            return Integer.compare(eagleDistance, state.getEagleDistance());
        } else if (aim != state.getAim()) {
            return Integer.compare(aim, getAim());
        } else {
            return Boolean.compare(explore, state.isExplore());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return enemyDistance == state.enemyDistance &&
                shellDistance == state.shellDistance &&
                eagleDistance == state.eagleDistance &&
                aim == state.aim &&
                explore == state.explore;
    }

    @Override
    public int hashCode() {

        return Objects.hash(enemyDistance, shellDistance, eagleDistance, aim, explore);
    }

    @Override
    public String toString() {
        return "State{" +
                "enemyDistance=" + enemyDistance +
                ", shellDistance=" + shellDistance +
                ", eagleDistance=" + eagleDistance +
                ", aim=" + aim +
                ", explore=" + explore +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("enemyDistance", enemyDistance);
        json.put("shellDistance", shellDistance);
        json.put("eagleDistance", eagleDistance);
        json.put("aim", aim);
        json.put("explore", explore);
        return json;
    }
}
