package com.issoft.conf.model;

import com.issoft.conf.GameMatrix;
import com.issoft.conf.action.Action;
import com.issoft.conf.actor.Agent;
import com.issoft.conf.vision.Vision;

public interface DecisionMaker<T> {

    void update(Agent agent, Action action, GameMatrix gameMatrix);

    void update(Vision vision, boolean killed);

    Action decision(T object);
}
