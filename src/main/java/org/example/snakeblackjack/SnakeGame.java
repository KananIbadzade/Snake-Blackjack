// SnakeGame.java
package org.example.snakeblackjack;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class SnakeGame extends Application {
    private static boolean inGame = false;
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
    private Direction direction = Direction.UP; //default direction
    private ArrayList<Rectangle> snakeBody = new ArrayList<>();
    private Rectangle snakeHead;
    private int score = 0;
    private int lastScore = 0;
    private Rectangle food;

    private Timeline timeline;
    private Timeline timeline2;

    private Stage primaryStage;



    private void gameLoop(Stage stage) {
        // Create an AnimationTimer that will run on each frame
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameInit();
                // Call the method that processes the game logic each frame
                handleKeyPressGameScene(stage);
                midGame(stage.getScene());
            }
        };
        gameLoop.start();  // Start the game loop

    }

    private void gameInit() {

    }


    public void midGame(Scene scene) {

        Rectangle snakeHead = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(1); //the snakehead
        Rectangle food = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(2); //the food
        Text actualScore = (Text)scene.getRoot().getChildrenUnmodifiable().get(3);
        double foodX = food.getX();
        double foodY = food.getY();
        //AtomicBoolean eaten = new AtomicBoolean(false);

        // Create the timeline only once
        if (timeline == null) {
            timeline = new Timeline(
                new KeyFrame(Duration.millis(30), e -> moveSnake(snakeHead)),
                new KeyFrame(Duration.millis(30), e -> checkFood(snakeHead, food, scene)), //score updated here
                new KeyFrame(Duration.millis(30), e -> updateScore(actualScore))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        timeline.play();


    }

    private void updateScore(Text actualScore) {
        if(lastScore == score) {
            return;
        }else{
            actualScore.setText(Integer.toString(score));
        }

        lastScore = score;

    }

    public void checkFood(Rectangle snakeHead, Rectangle food, Scene gameScene) { //pass in the current cord of the snakeHead
        if (snakeHead.getBoundsInParent().intersects(food.getBoundsInParent())) {
            double padding = 20;

// Scene dimensions
            double sceneWidth = gameScene.getWidth();
            double sceneHeight = gameScene.getHeight();

// Random generator
            Random random = new Random();

// Random X and Y within the padded area
            food.setX( padding + random.nextDouble() * (sceneWidth - 2 * padding));
            food.setY( padding + random.nextDouble() * (sceneHeight - 2 * padding));

            score++;

        }

    }

    public void handleKeyPressGameScene(Stage stage) {

        Scene gameScene = stage.getScene();

        // Using a traditional event handler instead of a lambda expression
        gameScene.setOnKeyPressed(new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP && !direction.equals(Direction.DOWN)) {
                    direction = Direction.UP;
                } else if (event.getCode() == KeyCode.DOWN && !direction.equals(Direction.UP)) {
                    direction = Direction.DOWN;
                } else if (event.getCode() == KeyCode.LEFT && !direction.equals(Direction.RIGHT)) {
                    direction = Direction.LEFT;
                } else if (event.getCode() == KeyCode.RIGHT && !direction.equals(Direction.LEFT)) {
                    direction = Direction.RIGHT;
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    inGame = false;
                    System.out.println("game Stopped");
                }
            }
        });

        gameScene.setOnMouseClicked(event -> {
            System.out.println("Mouse clicked at: X=" + event.getX() + ", Y=" + event.getY());
        });
    }

    private Rectangle getNewFood(Scene scene) {
        double padding = 20;

// Scene dimensions
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();

// Random generator
        Random random = new Random();

// Random X and Y within the padded area
        double x = padding + random.nextDouble() * (sceneWidth - 2 * padding);
        double y = padding + random.nextDouble() * (sceneHeight - 2 * padding);

// Create the rectangle
        Rectangle food =  new Rectangle(x, y, 20, 20); // 20x20 size
        food.setFill(Color.RED);
        return food;

    }

    // Snake Movement
    private void moveSnake(Rectangle snakeHead) {
        int UNIT_SIZE =5;
        if (direction.equals(Direction.UP)) {
            snakeHead.setY(snakeHead.getY() - UNIT_SIZE);
        } else if (direction.equals(Direction.DOWN)) {
            snakeHead.setY(snakeHead.getY() + UNIT_SIZE);
        } else if (direction.equals(Direction.LEFT)) {
            snakeHead.setX(snakeHead.getX() - UNIT_SIZE);
        } else if (direction.equals(Direction.RIGHT)) {
            snakeHead.setX(snakeHead.getX() + UNIT_SIZE);
        }
    }

    //this scene will be loaded when the game first started
    private Scene getPreScene(){
        Group root = new Group();
        Scene preScene = new Scene(root, 800, 600);
        preScene.setFill(Color.rgb(153,204,204)); //dark lightblue
        Label label = new Label("Press SPACE to start the game");
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        label.setLayoutX(250);
        label.setLayoutY(250);


        root.getChildren().add(label);
        return preScene;

    }

    private Scene getGameScene(){
        Group root = new Group();
        Scene gameScene = new Scene(root, 800, 600);
        gameScene.setFill(Color.rgb(204,255,255)); //lightblue

        Text text = new Text();
        text.setText("Score: ");
        text.setX(8);
        text.setY(20);
        text.setFont(Font.font(null, FontWeight.NORMAL, 20));
        text.setFill(Color.RED);

        Text actualScore = new Text(75, 22, "0");
        actualScore.setFont(Font.font(null, FontWeight.NORMAL, 20));
        actualScore.setFill(Color.rgb(153,204,102)); //light green


        Rectangle snakeHead = new Rectangle();
        snakeHead.setFill(Color.GREEN);
        snakeHead.setWidth(20);
        snakeHead.setHeight(20);
        snakeHead.setX(380);
        snakeHead.setY(250);

        Rectangle food = getNewFood(gameScene);


        root.getChildren().add(text);
        root.getChildren().add(snakeHead);
        root.getChildren().add(food);
        root.getChildren().add(actualScore);


        return gameScene;

    }


    @Override
    public void start(Stage stage) throws IOException {

        Scene preScene = getPreScene();
        Scene gameScene = getGameScene();

        FileInputStream fileInputStream = new FileInputStream(".\\src\\main\\resources\\icon.png");
        Image icon = new Image(fileInputStream);

        stage.getIcons().add(icon);
        stage.setTitle("Best Snake Game in the World");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(preScene);


        SnakeGame game = new SnakeGame();
        //game.midGame(root, scene, snakeHead, text);

        preScene.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.SPACE)) {
                stage.setScene(gameScene);
                inGame = true;
                gameLoop(stage);
            }
        });


        stage.show();

    }


    public static void launchGame(String[] args) {
        launch(args);  // start the JavaFX lifecycle
    }
}
