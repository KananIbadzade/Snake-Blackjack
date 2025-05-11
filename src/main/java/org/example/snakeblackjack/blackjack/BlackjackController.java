package org.example.snakeblackjack.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class BlackjackController {
    @FXML
    private GridPane tableGrid;  // the grid where we show names, cards, balances

    // user clicked the “Hit” button
    @FXML
    private void onHit() {
        BlackjackGame.getInstance().hit();
        refreshUI();
    }

    // user clicked the “Stand” button
    @FXML
    private void onStand() {
        BlackjackGame.getInstance().stand();
        refreshUI();
    }

    // user clicked “Save” → show a dialog with the save string to copy
    @FXML
    private void onSaveState() {
        String save = BlackjackGame.getInstance().getSaveString();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, save);
        alert.setHeaderText("Your save string (copy this):");
        alert.showAndWait();
    }

    // user clicked “Load” → prompt for a save string, then reload
    @FXML
    private void onLoadState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Paste your save string here:");
        dialog.setContentText("Save data:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            BlackjackGame.getInstance().loadFromString(result.get());
            refreshUI();
        }
    }

    // user clicked “Main Menu” → just close this window
    @FXML
    private void backToMainMenu() {
        Stage stage = (Stage) tableGrid.getScene().getWindow();
        stage.close();
    }

    // clear the grid and ask the game to draw the current state again
    private void refreshUI() {
        tableGrid.getChildren().clear();
        BlackjackGame.getInstance().render(tableGrid);
    }

    @FXML
    public void initialize() {
        refreshUI();  // this runs once after the scene is loaded
    }
}

