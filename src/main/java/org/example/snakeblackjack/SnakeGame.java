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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import javafx.geometry.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;


public class SnakeGame extends Application {
    private static boolean inGame = false;
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
    private double headX ;
    private double headY;
    private Direction direction = Direction.UP; //default direction

    private static ArrayList<BodyPart> snakeBody = new ArrayList<>(); //keep track of the snake body
    private int score = 0;
    private int lastScore = 0;
    private final double SPEED_BONUS = 0.1;
    private double speed = 5.0;
    //private Deque<Point2D> headTrail = new ArrayDeque<>();
    private int SPACING_FRAMES = ( (int) (30.0 / speed) + 1);
    private Text gameOverText;


    private Timeline timeline;

     private static class BodyPart{
         Deque<Point2D> nodeTrail = new ArrayDeque<>();
         Rectangle bodyPart;

         public BodyPart(Rectangle bodyPart){
             this.bodyPart = bodyPart;
             nodeTrail.addFirst(new Point2D(bodyPart.getX(), bodyPart.getY())); //store the init pos
         }

         public void setPosition (Point2D position){
             bodyPart.setX(position.getX());
             bodyPart.setY(position.getY());
         }

         public void addPosition(Point2D position){
             nodeTrail.addFirst(position);
         }

         public void updateDequeueSize(int frameSpace){
             //nodeTrail.addFirst(new Point2D(bodyPart.getX(), bodyPart.getY()));
             while(nodeTrail.size() > frameSpace){
                 nodeTrail.removeLast();
             } //always maintaining a dequeue such that the last element is the next render position
         }
         public Point2D getProperRenderPosition(){
             return nodeTrail.getLast();
         }
     }


    private void gameLoop(Stage stage) {
        // Create an AnimationTimer that will run on each frame
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(inGame){
                    gameInit();
                    // Call the method that processes the game logic each frame
                    handleKeyPressGameScene(stage);
                    midGame(stage, stage.getScene());
                }
            }
        };
        gameLoop.start();  // Start the game loop

    }

    private void gameInit() {

    }


    public void midGame(Stage stage, Scene scene) {

        Rectangle snakeHead = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(0); //the snakehead
        Rectangle food = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(2); //the food
        Text actualScore = (Text)scene.getRoot().getChildrenUnmodifiable().get(3);

        // Create the timeline only once
        if (timeline == null) {
            timeline = new Timeline(
                new KeyFrame(Duration.millis(20), e -> moveSnakeHead(snakeHead, speed)), // move the snake head for 1 frame
                new KeyFrame(Duration.millis(20), e -> moveSnakeBody(headX, headY)),
                new KeyFrame(Duration.millis(20), e -> checkFood(snakeHead, food, scene)), //score updated here
                new KeyFrame(Duration.millis(20), e -> snakeWallCollision(snakeHead, stage)),
                new KeyFrame(Duration.millis(20), e -> snakeBodyCollision(snakeHead, stage)),
                new KeyFrame(Duration.millis(20), e -> updateScore(actualScore))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.play();
    }



    //factory method to create snake body parts and add them to the snake body ArrayList
    public void getNewSnakeBodyPart(double initX, double initY, Group root) {
        Point2D renderPos = snakeBody.isEmpty() ? new Point2D(initX, initY)
            : snakeBody.getLast().getProperRenderPosition();

        Rectangle bodyPart = new Rectangle(renderPos.getX(), renderPos.getY(), 20, 20);
        bodyPart.setFill(Color.rgb(51, 255, 153)); //neon green for body parts
        bodyPart.setStroke(Color.rgb(0,0,0)); //show the outline
        if(snakeBody.isEmpty()){bodyPart.setFill(Color.rgb(28,122,44));} // dark green for snake head

        BodyPart newPart = new BodyPart(bodyPart);
        for (int i = 0; i < SPACING_FRAMES; i++) {
            newPart.addPosition(new Point2D(renderPos.getX(), renderPos.getY()));
        }

        snakeBody.add(newPart); //add to snakeBody list
        root.getChildren().add(bodyPart); //render
    }

    // Snake Movement
    private void moveSnakeHead(Rectangle snakeHead, double UNIT_SIZE) {

        if (direction.equals(Direction.UP)) {
            snakeHead.setY(snakeHead.getY() - UNIT_SIZE);
        } else if (direction.equals(Direction.DOWN)) {
            snakeHead.setY(snakeHead.getY() + UNIT_SIZE);
        } else if (direction.equals(Direction.LEFT)) {
            snakeHead.setX(snakeHead.getX() - UNIT_SIZE);
        } else if (direction.equals(Direction.RIGHT)) {
            snakeHead.setX(snakeHead.getX() + UNIT_SIZE);
        }
        SPACING_FRAMES = ((int) (30.0 / UNIT_SIZE) + 1);
        snakeBody.getFirst().addPosition(new Point2D(snakeHead.getX(), snakeHead.getY()));
        snakeBody.getFirst().updateDequeueSize(SPACING_FRAMES); //update the snakeHead

    }

    private void updatePosition(double speed) {
         int frameSpace = ((int) (30.0 / speed) + 1);
         snakeBody.forEach(bodyPart -> bodyPart.updateDequeueSize(frameSpace));
    }


    public void moveSnakeBody(double snakeHeadX, double snakeHeadY) {

         if(snakeBody.size() == 1){ return;} // early return if there's only the head

        //if there are more than 0 body parts
         ListIterator<BodyPart> iterator = snakeBody.listIterator(1);
         int previousIndex = 0; // start from the head as the first previous index
         SPACING_FRAMES = ( (int) (30.0 / speed) + 1);


        BodyPart previous = snakeBody.getFirst(); // start from the head

        while (iterator.hasNext()) {
            BodyPart current = iterator.next();
            Point2D nextPos = previous.getProperRenderPosition();
            current.addPosition(nextPos);
            current.setPosition(nextPos);
            current.updateDequeueSize(SPACING_FRAMES);
            previous = current;
        }

    }
    public void snakeBodyCollision(Rectangle snakeHead, Stage stage) {
         if(snakeBody.size() > 3) {
             for (int i = 1; i < snakeBody.size(); i++) {
                 BodyPart bodyPart = snakeBody.get(i);

                 double disX = snakeHead.getX() - bodyPart.bodyPart.getX();
                 double disY = snakeHead.getY() - bodyPart.bodyPart.getY();
                 double distance = Math.sqrt(disX * disX + disY * disY);

                 if (distance < 10) {
                     gameOver(stage);
                     break;
                 }
             }
         }
    }

    public void snakeWallCollision(Rectangle snakeHead, Stage stage) {
        double sceneWidth = stage.getScene().getWidth();
        double sceneHeight = stage.getScene().getHeight();

        if (snakeHead.getX() < 0 || snakeHead.getX() > sceneWidth - snakeHead.getWidth() ||
                snakeHead.getY() < 0 || snakeHead.getY() > sceneHeight - snakeHead.getHeight()) {

            gameOver(stage);
        }
    }
    private void gameOver(Stage stage) {
        timeline.stop();
        inGame = false;
        System.out.println("Game Over!");
        gameOverText.setVisible(true);

        //stage.setScene(getPreScene());
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

            //actions
            speed += SPEED_BONUS; //increment speed
            score++; //increment score
            Point2D position = snakeBody.getLast().getProperRenderPosition();
            getNewSnakeBodyPart(position.getX(), position.getY(), (Group)gameScene.getRoot());

        }

    }


    private void updateScore(Text actualScore) {
        if(lastScore == score) {
            return;
        }else{
            actualScore.setText(Integer.toString(score));
        }

        lastScore = score;

    }



    public void handleKeyPressGameScene(Stage stage) {

        Scene gameScene = stage.getScene();

        // Using a traditional event handler instead of a lambda expression
        gameScene.setOnKeyPressed(new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if(!inGame) {
                    return;
                }
                System.out.println("Speed: " + speed);
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
                    //stage.setScene(getPreScene());
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
        actualScore.setFill(Color.rgb(204,102,0)); //dark orange


        getNewSnakeBodyPart(380, 250, root); //init the snakebody with the snake head
        //snake head is automatically added to the root

        Rectangle food = getNewFood(gameScene);

        gameOverText = new Text("GAME OVER");
        gameOverText.setX(250);  // depends on your scene width
        gameOverText.setY(300);
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        gameOverText.setVisible(false);


        root.getChildren().add(text);
        root.getChildren().add(food);
        root.getChildren().add(actualScore);

        root.getChildren().add(gameOverText);


        return gameScene;

    }


    @Override
    public void start(Stage stage) throws IOException {

        Scene preScene = getPreScene();
        Scene gameScene = getGameScene();

        /* ocmmented out setting the icon code May.01.25
        InputStream input = getClass().getResourceAsStream("/icon.png");
        Image icon = new Image(input);

        stage.getIcons().add(icon); */
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
