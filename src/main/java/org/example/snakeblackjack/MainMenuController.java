package org.example.snakeblackjack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private void launchSnake() throws Exception {
        Stage snakeStage = new Stage();
        SnakeGame.launchGame(snakeStage);
     }

    @FXML
    private void launchBlackjack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/blackjack.fxml"));
        Stage stage = (Stage) javafx.stage.Window.getWindows()
                .filtered(w -> w.isShowing()).get(0);
        stage.setScene(new Scene(root));
    }
}
