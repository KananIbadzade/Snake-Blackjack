package org.example.snakeblackjack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.snakeblackjack.blackjack.BlackjackController;
import javafx.scene.control.TextArea;
import org.example.snakeblackjack.blackjack.BlackjackGame;

import java.awt.*;
import java.util.Map;
import java.util.List;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextArea topScoresArea;

    @FXML
    public TextArea topScoresAreaBlkJk;

    @FXML
    private void launchSnake() throws Exception {
        // Get the current stage
        Stage stage = (Stage) javafx.stage.Window.getWindows()
            .filtered(w -> w.isShowing()).get(0);

        // Create a SnakeGame instance
        SnakeGame game = new SnakeGame();
        game.setStage(stage); //for returning to mainmenu
        // Pass in username and score manager
        game.setUsername(LoginController.loggedInUserName); // pass logged in user
        game.setScoreManager(new HighScoreManager());   // pass score manager

        // Start the game
        game.start(stage);
    }



    @FXML
    private void launchBlackjack() throws Exception {
        Stage stage = (Stage) javafx.stage.Window.getWindows()
            .filtered(w -> w.isShowing()).get(0);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/blackjack.fxml"));
        Parent root = loader.load();
        BlackjackController controller = loader.getController();
        controller.setStage(stage);

        BlackjackGame game = BlackjackGame.getInstance();
        // Pass in username and score manager
        game.setUsername(LoginController.loggedInUserName); // pass logged in user
        game.setScoreManager(new HighScoreManager());   // pass score manager

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void initialize() {
        String username = LoginController.loggedInUserName;
        if (username != null) {
            welcomeLabel.setText("Welcome, " + username);
            HighScoreManager scoreManager = new HighScoreManager();



            scoreManager.defaultScoresForUsers(username);

            List<Map.Entry<String, Integer>> top5 = scoreManager.getTop5SnakeScores();
            StringBuilder sb = new StringBuilder("🏆 Top 5 Snake Scores:\n");
            int rank = 1;
            for (Map.Entry<String, Integer> entry : top5) {
                sb.append(rank++).append(". ")
                        .append(entry.getKey()).append(" - ")
                        .append(entry.getValue()).append("\n");
            }
            topScoresArea.setText(sb.toString());

            List<Map.Entry<String, Integer>> top5BlkJk = scoreManager.getTop5BlackScores();
            StringBuilder sbBlkJk = new StringBuilder("🏆 Top 5 BlackJack Scores (Rounds Won):\n");
            int rankBlkJk = 1;
            for (Map.Entry<String, Integer> entry : top5BlkJk) {
                sbBlkJk.append(rankBlkJk++).append(". ")
                    .append(entry.getKey()).append(" - ")
                    .append(entry.getValue()).append("\n");
            }
            topScoresAreaBlkJk.setText(sbBlkJk.toString());
        }
    }

}
