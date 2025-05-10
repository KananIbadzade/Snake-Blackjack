package org.example.snakeblackjack;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML
    private void launchSnake() throws Exception {
        // reuse your existing SnakeGame
        new SnakeGame().start((Stage) /* get any node’s stage */
                javafx.stage.Window.getWindows().filtered(w->w.isShowing()).get(0));
    }

    @FXML
    private void launchBlackjack() throws Exception {
        // hand off to your BlackjackMain
        new org.example.snakeblackjack.blackjack.BlackjackMain()
                .start((Stage) javafx.stage.Window.getWindows()
                        .filtered(w->w.isShowing()).get(0));
    }
}
