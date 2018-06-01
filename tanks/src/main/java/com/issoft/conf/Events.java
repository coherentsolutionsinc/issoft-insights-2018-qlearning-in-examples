package com.issoft.conf;

import com.issoft.conf.actor.DynamicObject;
import javafx.scene.input.KeyCode;

public interface Events {

    void nextStep(DynamicObject dynamicObject);

    void keyPress(KeyCode keyCode);

    void show();

    void model();

}
