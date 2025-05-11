package org.example.snakeblackjack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label snakeScoreLabel;
    @FXML
    private Label blackjackScoreLabel;

    @FXML
    private void launchSnake() throws Exception {
        Stage snakeStage = new Stage();
        //SnakeGame.launchGame(snakeStage);


        // Get the current stage
        Stage stage = (Stage) javafx.stage.Window.getWindows()
            .filtered(w -> w.isShowing()).get(0);

        // Create a SnakeGame instance
        SnakeGame game = new SnakeGame();

        // Pass in username and score manager
        game.setUsername(LoginController.loggedInUserName); // pass logged in user
        game.setScoreManager(new HighScoreManager());   // pass score manager

        // Start the game
        game.start(stage);
    }



@FXML
private void launchBlackjack() throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/blackjack.fxml"));

    Stage stage = (Stage) javafx.stage.Window.getWindows()
        .filtered(w -> w.isShowing()).get(0);
    stage.setScene(new Scene(root));
    stage.show();
}

@FXML
public void initialize() {
    String username = LoginController.loggedInUserName;
    if (username != null) {
        welcomeLabel.setText("Welcome, " + username);
        HighScoreManager scoreManager = new HighScoreManager();
        snakeScoreLabel.setText("Snake High Score: " + scoreManager.getSnakeScore(username));
        blackjackScoreLabel.setText("Blackjack High Score: " + scoreManager.getBlackjackScore(username));
    }
}

}
