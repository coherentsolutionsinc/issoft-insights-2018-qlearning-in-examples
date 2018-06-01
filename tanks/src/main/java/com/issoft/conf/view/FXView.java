package com.issoft.conf.view;

import com.issoft.conf.Events;
import com.issoft.conf.Location;
import com.issoft.conf.actor.*;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import static com.issoft.conf.constants.Constants.DIM;
import static com.issoft.conf.constants.Constants.STEP_DURATION;

public class FXView implements GameView {

    private static final double SCALE = 50;

    private Group group;

    private Canvas layer;
    private GraphicsContext gc;

    private Events events;

    private Map<String, TranslateTransition> objectsMap = new HashMap<>();

    private static final Map<Direction, ImagePattern> AGENT_VIEW = new HashMap<>();

    private boolean training;
    private int speed;

    static {
        AGENT_VIEW.put(Direction.UP, new ImagePattern(new Image(FXView.class.getClassLoader().getResource("agent/UP.png").toString())));
        AGENT_VIEW.put(Direction.DOWN, new ImagePattern(new Image(FXView.class.getClassLoader().getResource("agent/DOWN.png").toString())));
        AGENT_VIEW.put(Direction.LEFT, new ImagePattern(new Image(FXView.class.getClassLoader().getResource("agent/LEFT.png").toString())));
        AGENT_VIEW.put(Direction.RIGHT, new ImagePattern(new Image(FXView.class.getClassLoader().getResource("agent/RIGHT.png").toString())));
    }

    public FXView(boolean training, int speed) {
        this.training = training;
        this.speed = speed;
    }

    @Override
    public void init(Events events) {
        this.events = events;
    }

    @Override
    public void update(Agent agent) {
        TranslateTransition transition = objectsMap.get(agent.getId());
        if (transition != null) {
            ((Shape) transition.getNode()).setFill(AGENT_VIEW.get(agent.getDirection()));
            transition.setToX(agent.getLocation().getY() * SCALE - agent.getInitialLocation().getY() * SCALE);
            transition.setToY(agent.getLocation().getX() * SCALE - agent.getInitialLocation().getX() * SCALE);
            transition.playFromStart();
        }
    }

    @Override
    public void update(Shell shell) {
        TranslateTransition transition = objectsMap.get(shell.getId());
        transition.setToX(shell.getLocation().getY() * SCALE - shell.getInitialLocation().getY() * SCALE);
        transition.setToY(shell.getLocation().getX() * SCALE - shell.getInitialLocation().getX() * SCALE);
        transition.playFromStart();
    }

    @Override
    public void delete(Agent agent) {
        deleteDynamicObject(agent);
    }

    @Override
    public void delete(Shell shell) {
        deleteDynamicObject(shell);
    }

    public void deleteDynamicObject(DynamicObject dynamicObject) {
        TranslateTransition toRemove = objectsMap.remove(dynamicObject.getId());
        if (toRemove != null) {
            Node node = toRemove.getNode();
            group.getChildren().remove(node);
        }
    }

    @Override
    public void explode(Shell shell) {
        Node node = objectsMap.get(shell.getId()).getNode();
        Circle circle = (Circle) node;
        circle.setRadius(SCALE / 3);
        circle.setFill(Color.RED);
        final TranslateTransition transition = new TranslateTransition(Duration.seconds(STEP_DURATION / 2 / speed), circle);
        transition.setOnFinished(event -> {
            delete(shell);
        });
        transition.playFromStart();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tanks");
        layer = new Canvas(DIM * SCALE, DIM * SCALE);
        gc = layer.getGraphicsContext2D();
        group = new Group(layer);

        final Scene scene = new Scene(group, DIM * SCALE, DIM * SCALE, Color.WHITE);

        scene.setOnKeyPressed(event -> events.keyPress(event.getCode()));
        scene.getRoot().requestFocus();

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void drawEagle(Eagle eagle) {
        Rectangle rectangle = createEagle(eagle.getLocation());
        group.getChildren().add(rectangle);
    }

    @Override
    public void drawWall(Wall wall) {
        gc.setFill(Color.GRAY);
        gc.fillRect(wall.getLocation().getY() * SCALE, wall.getLocation().getX() * SCALE, SCALE, SCALE);
    }

    @Override
    public void drawAgent(Agent agent) {
        Rectangle rectangle = createAgent(agent.getLocation(), agent.getDirection());
        TranslateTransition transition = createTranslateTransition(agent, rectangle);
        objectsMap.put(agent.getId(), transition);
        group.getChildren().add(rectangle);
    }

    @Override
    public void drawShell(Shell shell) {
        Circle circle = createShell(shell.getLocation());
        TranslateTransition transition = createTranslateTransition(shell, circle);
        objectsMap.put(shell.getId(), transition);
        group.getChildren().add(circle);
    }

    @Override
    public boolean training() {
        return training;
    }

    private Rectangle createAgent(Location location, Direction direction) {
        Rectangle rectangle = new Rectangle(location.getY() * SCALE, location.getX() * SCALE, SCALE, SCALE);
        rectangle.setFill(AGENT_VIEW.get(direction));
        return rectangle;
    }

    private Rectangle createEagle(Location location) {
        Rectangle rectangle = new Rectangle(location.getY() * SCALE, location.getX() * SCALE, SCALE, SCALE);
        ImagePattern imagePattern = new ImagePattern(new Image(FXView.class.getClassLoader().getResource("eagle.png").toString()));
        rectangle.setFill(imagePattern);
        return rectangle;
    }

    private Circle createShell(Location location) {
        Circle circle = new Circle(location.getY() * SCALE + SCALE / 2, location.getX() * SCALE + SCALE / 2, SCALE / 5);
        circle.setFill(Color.BLACK);
        return circle;
    }

    private TranslateTransition createTranslateTransition(Agent agent, final Rectangle rectangle) {
        final TranslateTransition transition = new TranslateTransition(Duration.seconds(3 * STEP_DURATION / speed), rectangle);
        transition.setOnFinished(t -> {
            events.nextStep(agent);
        });
        return transition;
    }

    private TranslateTransition createTranslateTransition(Shell shell, final Circle circle) {
        final TranslateTransition transition = new TranslateTransition(Duration.seconds(STEP_DURATION / speed), circle);
        transition.setOnFinished(t -> {
            events.nextStep(shell);
        });
        return transition;
    }

}
