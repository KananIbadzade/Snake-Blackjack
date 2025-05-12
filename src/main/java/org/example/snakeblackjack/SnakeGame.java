// SnakeGame.java
package org.example.snakeblackjack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ListIterator;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SnakeGame extends Application {
    private static boolean inGame = false;
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
    private double headX ;
    private double headY;
    private Direction direction = Direction.UP; //default direction

    //initial position of snakeHead
    private final double SNAKE_HEAD_INIT_X = 380;
    private final double SNAKE_HEAD_INIT_Y = 275;

    private static ArrayList<BodyPart> snakeBody = new ArrayList<>(); //keep track of the snake body
    private int score = 0;
    private int lastScore = 0;
    private final double SPEED_BONUS = 0.1;
    private double speed = 5.0;
    //private Deque<Point2D> headTrail = new ArrayDeque<>();
    private int SPACING_FRAMES = ( (int) (30.0 / speed) + 1);
    private Text gameOverText;
    private TextFlow gameRestartHint;
    private Rectangle wallBox;

    private Timeline timeline;
    private AnimationTimer gameLoop;

    private String currentUserName;
    private HighScoreManager scoreManager;

    private MediaPlayer mediaPlayer;

    private Stage stageForMenu;

    public void setStage(Stage stage) {
        stageForMenu = stage;
    }

    private static class BodyPart{
         Deque<Point2D> nodeTrail = new ArrayDeque<>();
         Rectangle bodyPart;

         public BodyPart(Rectangle bodyPart){
             this.bodyPart = bodyPart;
             nodeTrail.addFirst(new Point2D(bodyPart.getX(), bodyPart.getY())); //store the init pos
         }

         public Point2D getCurrentPosition(){
             return new Point2D(bodyPart.getX(), bodyPart.getY());
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


    private void startGameLoop(Stage stage) {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (inGame) {
                        handleKeyPressGameScene(stage);
                        midGame(stage, stage.getScene());
                    }
                }
            };
        }
        gameLoop.start(); // restart if previously stopped
    }

    private void restartGame(Stage stage) {
        inGame = false;  // Stop logic temporarily

        //reset the game state
        score = 0;
        lastScore = 0;
        direction = Direction.UP;
        speed = 5.0; //reset to default speed
        gameOverText.setVisible(false);
        gameRestartHint.setVisible(false);
        timeline = null;

        snakeBody.clear();

        Scene scene = getGameScene();
        stage.setScene(scene);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.play();
        }

        inGame = true; // resume the game
        startGameLoop(stage); // this just starts the same AnimationTimer again

    }


    public void midGame(Stage stage, Scene scene) {

        Rectangle snakeHead = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(1); //the snakehead
        Rectangle food = (Rectangle)scene.getRoot().getChildrenUnmodifiable().get(3); //the food
        Text actualScore = (Text)scene.getRoot().getChildrenUnmodifiable().get(4);

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
        if(snakeBody.isEmpty()){
            bodyPart.setFill(Color.rgb(28,122,44));} // dark green for snake head
        else{bodyPart.setFill(Color.rgb(51, 255, 153));} //neon green for body parts

        bodyPart.setStroke(Color.rgb(0,0,0)); //show the outline


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

         BodyPart nowChecking;
         BodyPart snakeHeadBodyPart = snakeBody.getFirst();
         if(snakeBody.size() > 3) {
             for (int i = 2; i < snakeBody.size(); i++) {
                 nowChecking = snakeBody.get(i);

                 if (nowChecking.getCurrentPosition().distance(snakeHeadBodyPart.getCurrentPosition()) > 50) {
                     continue; //skip checking if the distance is too far away
                 }
                 if (nowChecking.bodyPart.getBoundsInParent().intersects(snakeHead.getBoundsInParent())) {
                     timeline.stop();
                     gameOver(stage);
                 }
             }
         }
    }

    public void snakeWallCollision(Rectangle snakeHead, Stage stage) {
        double buffer = 4; // Simulate stroke width

        //outer bounds of the snakeHead
        double headX = snakeHead.getX();
        double headY = snakeHead.getY();
        double headWidth = snakeHead.getWidth();
        double headHeight = snakeHead.getHeight();

        //inner Bounds of the rectangle
        double left = wallBox.getX();
        double right = wallBox.getX() + wallBox.getWidth();
        double top = wallBox.getY();
        double bottom = wallBox.getY() + wallBox.getHeight();

        // Check if head is within the stroke area (3-pixel-wide band)
        boolean touchLeft = headX <= left + buffer && headX + headWidth >= left;
        boolean touchRight = headX + headWidth >= right - buffer + 1; //offset on the right boarder
        boolean touchTop = headY <= top + buffer && headY + headHeight >= top;
        boolean touchBottom = headY + headHeight >= bottom - buffer;

        if (touchLeft || touchRight || touchTop || touchBottom) {
            System.out.println("Collision with stroke only.");
            gameOver(stage);
        }
    }

    private void gameOver(Stage stage) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();  // stop music on game over
        }
        timeline.stop();
        inGame = false;

        if (scoreManager != null && currentUserName != null) {
            int oldScore = scoreManager.getSnakeScore(currentUserName);
            System.out.println("OLD SCORE: " + oldScore);
            System.out.println("NEW SCORE: " + score);

            if (oldScore == 1000 || score > oldScore) {
                scoreManager.updateSnakeScore(currentUserName, score);
                System.out.println("Snake score updated to: " + score);
            }
        }

        double textWidth = gameOverText.getLayoutBounds().getWidth();
        double textHeight = gameOverText.getLayoutBounds().getHeight();
        double rectCenterX = wallBox.getX() + wallBox.getWidth() / 2;
        double rectCenterY = wallBox.getY() + wallBox.getHeight() / 2;

        gameOverText.setX(rectCenterX - textWidth / 2);
        gameRestartHint.setLayoutX(gameOverText.getX() + 10);
        gameOverText.setY(rectCenterY + textHeight / 4);
        gameRestartHint.setLayoutY(gameOverText.getY() + 35);

        System.out.println("Game Over!");
        gameOverText.setVisible(true);
        gameRestartHint.setVisible(true);
        gameRestartHint.toFront();
        gameOverText.toFront();
    }

    public void checkFood(Rectangle snakeHead, Rectangle currentFood, Scene gameScene) { //pass in the current cord of the snakeHead
        if (snakeHead.getBoundsInParent().intersects(currentFood.getBoundsInParent())) {
            Rectangle newFood = getNewFood(gameScene);
            currentFood.setX(newFood.getX());
            currentFood.setY(newFood.getY());

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
                    if(event.getCode() == KeyCode.SPACE) {


                        event.consume();

                        restartGame(stage);
                    }
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
                event.consume();
            }
        });

        gameScene.setOnMouseClicked(event -> {
            System.out.println("Mouse clicked at: X=" + event.getX() + ", Y=" + event.getY());
            event.consume();
        });
    }

    private Rectangle getNewFood(Scene scene) {
        double margin = 3;
        double rectWidth = 20;
        double rectHeight = 20;

        double minX = wallBox.getX() + margin;
        double maxX = wallBox.getX() + wallBox.getWidth() - margin - rectWidth;

        double minY = wallBox.getY() + margin;
        double maxY = wallBox.getY() + wallBox.getHeight() - margin - rectHeight;

        double randomX = minX + Math.random() * (maxX - minX);
        double randomY = minY + Math.random() * (maxY - minY);
        BodyPart nowChecking;
        int i = 1;
        Point2D newFoodPos = new Point2D(randomX, randomY);

        // avoiding the food to generate inside the body
        while(i < snakeBody.size()) {
            nowChecking = snakeBody.get(i);

            if (nowChecking.getCurrentPosition().distance(newFoodPos) >= 45) {
                i++;
                continue; //skip checking if the distance is too far away
            }
            if (nowChecking.getCurrentPosition().distance(newFoodPos) < 45) {
                i = 1; //recheck from the first body part
                randomX = minX + Math.random() * (maxX - minX);
                randomY = minY + Math.random() * (maxY - minY);
                newFoodPos = new Point2D (randomX, randomY);
            }
        }

        Rectangle food = new Rectangle(randomX, randomY, rectWidth, rectHeight);

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

        MenuBar menuBar = new MenuBar();
        menuBar.setPrefWidth(800);

        // Game Menu
        Menu gameMenu = new Menu("Game");
        MenuItem pauseMenuItem = new MenuItem("Pause");
        MenuItem restartMenuItem = new MenuItem("Restart");
        MenuItem returnToMenu = new MenuItem("Return to Menu");

        pauseMenuItem.setOnAction(e -> {
            if (inGame) {
                timeline.pause();
                inGame = false;
                pauseMenuItem.setText("Resume");
            } else {
                timeline.play();
                inGame = true;
                pauseMenuItem.setText("Pause");
            }
        });

        restartMenuItem.setOnAction(e -> {
            restartGame((Stage) gameScene.getWindow());
        });

        returnToMenu.setOnAction(e -> {
            Stage stage = (Stage) gameScene.getWindow();

            timeline.stop();
            mediaPlayer.stop();

            //reset the game state
            score = 0;
            lastScore = 0;
            direction = Direction.UP;
            speed = 5.0; //reset to default speed
            gameOverText.setVisible(false);
            gameRestartHint.setVisible(false);


            snakeBody.clear();

            stage.close();

            Parent mainMenuRoot = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainmenu.fxml"));
                mainMenuRoot = loader.load();
            }catch (Exception e1) { System.out.println("Error loading main menu");}
            stageForMenu.setWidth(666);
            stageForMenu.setHeight(417);
            stageForMenu.setTitle("MainMenu");
            stageForMenu.setScene(new Scene(mainMenuRoot));
            stageForMenu.setResizable(false);
            stageForMenu.show();
            e.consume();

        });

        gameMenu.getItems().addAll(pauseMenuItem, restartMenuItem, returnToMenu);



        menuBar.getMenus().addAll(gameMenu);

        // Add the menu bar to the root
        root.getChildren().add(menuBar);

        // The rest of your existing game scene setup
        // IMPORTANT: Adjust the Y positions of your game elements to avoid overlapping with the menu bar
        double menuHeight = 25; // Approximate menu bar height

        wallBox = new Rectangle(8, 30 + menuHeight, 770, 520 - menuHeight);

        //wallBox = new Rectangle(8, 30, 770, 520);
        wallBox.setStroke(Color.web("231D2C"));
        wallBox.setStrokeWidth(3);
        Glow glow = new Glow();
        glow.setLevel(0.2);
        wallBox.setEffect(glow);

        wallBox.setFill(Color.TRANSPARENT);

        Text text = new Text();
        text.setText("Score: ");
        text.setX(8);
        text.setY(45);
        text.setFont(Font.font(null, FontWeight.NORMAL, 20));
        text.setFill(Color.RED);

        Text actualScore = new Text(75, 45, "0");
        actualScore.setFont(Font.font(null, FontWeight.NORMAL, 20));
        actualScore.setFill(Color.rgb(204,102,0)); //dark orange


        getNewSnakeBodyPart(SNAKE_HEAD_INIT_X, SNAKE_HEAD_INIT_Y + 25, root); //init the snakebody with the snake head
        //snake head is automatically added to the root

        Rectangle food = getNewFood(gameScene);

        gameOverText = new Text("GAME OVER");
        gameOverText.setX(250);  // depends on your scene width
        gameOverText.setY(325);
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        gameOverText.setVisible(false);

        Text hintFirstPortion = new Text("Press ");
        hintFirstPortion.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        hintFirstPortion.setFill(Color.web("22b078"));

        Text hintItalicPortion = new Text("SPACE");
        hintItalicPortion.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 20));
        hintItalicPortion.setFill(Color.web("22b078"));


        Text hintLastPortion = new Text(" to restart");
        hintLastPortion.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        hintLastPortion.setFill(Color.web("22b078"));

        gameRestartHint = new TextFlow(hintFirstPortion, hintItalicPortion, hintLastPortion);
        gameRestartHint.setVisible(false);
        gameOverText.setVisible(false);


        root.getChildren().add(text);
        root.getChildren().add(food);
        root.getChildren().add(actualScore);

        root.getChildren().add(gameOverText);
        root.getChildren().add(gameRestartHint);
        root.getChildren().add(wallBox);


        return gameScene;

    }

    @Override
    public void start(Stage stage) throws IOException {

        Scene preScene = getPreScene();
        Scene gameScene = getGameScene();

        /* commented out setting the icon code May.01.25
        InputStream input = getClass().getResourceAsStream("/icon.png");
        Image icon = new Image(input);
        Image image = new Image("https://drive.google.com/uc?export=view&id=1mnLEnbq6SLHUx_sIfNVAlQJK7Yc9f0zp");

        stage.getIcons().add(image);*/
        stage.setTitle("Best Snake Game in the World");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(preScene);
        preScene.getRoot().requestFocus();

        SnakeGame game = new SnakeGame();
        //game.midGame(root, scene, snakeHead, text);

        preScene.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.SPACE)) {
                event.consume();
                stage.setScene(gameScene);
                gameScene.getRoot().requestFocus();
                inGame = true;
                startGameLoop(stage);
            }
        });

        String musicFile = "/audio/snakeMusic.mp3";
        Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        stage.show();

    }


//    public static void launchGame(Stage stage) {
//        try{new SnakeGame().start(stage);}
//        catch (IOException e){ System.out.println(e.getMessage());}// start the JavaFX lifecycle
//    }

    public void setUsername(String username) {
        this.currentUserName = username;
    }

    public void setScoreManager(HighScoreManager manager) {
            this.scoreManager = manager;
        }

    public void launchGame(Stage stage, String username) {
            try {
                SnakeGame game = new SnakeGame();
                HighScoreManager manager = new HighScoreManager();
                //manager.defaultScoresForUsers(username);
                game.setUsername(username);
                game.setScoreManager(new HighScoreManager());
                game.setStage(stage);
                game.start(stage);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

}



}
