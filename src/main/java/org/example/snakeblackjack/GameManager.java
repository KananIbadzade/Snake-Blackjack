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

        //calling black jack game
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/snakeblackjack/blackjack.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        }catch (Exception e){System.out.println(e.getMessage());}

        blackJackStage.setScene(new Scene(root));
        blackJackStage.setTitle("Black Jack He He");
        blackJackStage.show();


    }

    public static void launchManager(String[] args) {
        launch(args);
    }


}
