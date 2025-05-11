package org.example.snakeblackjack.blackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class BlackjackMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/blackjack_Sean_version.fxml"));
        Stage blackJackStage = loader.load();
        stage.setTitle("Blackjack");
        //stage.setScene(new Scene(root));
        stage.setResizable(false);
        blackJackStage.show();
    }
}
