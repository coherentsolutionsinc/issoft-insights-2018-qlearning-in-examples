package com.issoft.conf;

import com.issoft.conf.view.ConsoleView;
import com.issoft.conf.view.FXView;
import com.issoft.conf.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class FXGame extends Application {

    private static final boolean DEFAULT_TRAINING = false;
    private static final int DEFAULT_MODEL_NUMBER = 5;
    private static final int DEFAULT_SPEED = 1;

    public static void main(String[] args) {
        launch(FXGame.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        List<String> args = getParameters().getRaw();

        int modelNumber = DEFAULT_MODEL_NUMBER;
        boolean training = DEFAULT_TRAINING;
        int speed = DEFAULT_SPEED;

        if (args.size() == 0) {
            System.out.println("No arguments");
            return;
        }
        String map = args.get(0);

        if (args.size() > 1) {
            modelNumber = Integer.parseInt(args.get(1));
        }
        if (args.size() > 2) {
            speed = Integer.parseInt(args.get(2));
        }

        if (modelNumber == 0) {
            training = true;
        }

        GameView gameView = null;
        if (speed == 0) {
            gameView = new ConsoleView(training);
        } else {
            gameView = new FXView(training, speed);
        }

        GameController gameController = new GameControllerImpl(gameView, new GameMatrixImpl(map));
        gameController.start(primaryStage, modelNumber);
    }

}
