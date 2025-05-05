package org.example.snakeblackjack.blackjack;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BlackjackController {
    @FXML private GridPane tableGrid;

    // called by <Button onAction="#onHit"/>
    @FXML private void onHit() {
        BlackjackGame.getInstance().hit();
        refreshUI();
    }

    // called by <Button onAction="#onStand"/>
    @FXML private void onStand() {
        BlackjackGame.getInstance().stand();
        refreshUI();
    }

    @FXML private void onSaveState() {
        String save = BlackjackGame.getInstance().getSaveString();
        Alert a = new Alert(Alert.AlertType.INFORMATION, save);
        a.setHeaderText("Copy this to reload later");
        a.showAndWait();
    }

    @FXML private void onLoadState() {
        // prompt user for save string, then:
        // BlackjackGame.getInstance().loadFromString(input);
        refreshUI();
    }

    @FXML private void backToMainMenu() {
        // simply close this window to return to your Main menu
        Stage s = (Stage) tableGrid.getScene().getWindow();
        s.close();
    }

    private void refreshUI() {
        tableGrid.getChildren().clear();
        BlackjackGame.getInstance().render(tableGrid);
    }
}
