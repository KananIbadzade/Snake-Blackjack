package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameManager extends Application {

    public static void launchManager(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
             SnakeGame.launchGame();
             BlackJack.launchGame();

    }
}
