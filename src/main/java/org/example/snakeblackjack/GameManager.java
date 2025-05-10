package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameManager extends Application {


    @Override
    public void start(Stage primaryStage) {
        Stage blackJackStage = new Stage();
        Stage snakeStage = new Stage();
        SnakeGame.launchGame(snakeStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/blackjack.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        }catch (Exception e){System.out.println(e.getMessage());}

        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Black Jack He He");
        newStage.show();


    }

    public static void launchManager(String[] args) {
        launch(args);
    }


}
