package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameManager extends Application {


    @Override
    public void start(Stage primaryStage) {
        Stage blackJackStage = new Stage();
        Stage snakeStage = new Stage();
        SnakeGame.launchGame(snakeStage);
        BlackJack.launchGame(blackJackStage);

    }

    public static void launchManager(String[] args) {
        launch(args);
    }


}
