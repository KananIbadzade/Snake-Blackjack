package org.example.snakeblackjack.blackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BlackjackMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/blackjack.fxml"));
        Parent root = loader.load();
        stage.setTitle("Blackjack");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}
