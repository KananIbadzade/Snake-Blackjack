package org.example.snakeblackjack.blackjack;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class BlackjackController {
    @FXML private GridPane tableGrid;
    @FXML private Label turnLabel;
    @FXML private Label roundLabel;
    @FXML private Label statusLabel;
    @FXML private Button hitButton;
    @FXML private Button standButton;

    // user clicked the “Hit” button
    @FXML
    private void onHit() {
        BlackjackGame.getInstance().hit();
        refreshUI();
    }

    // user clicked the “Stand” button
    @FXML
    private void onStand() {
        BlackjackGame game = BlackjackGame.getInstance();
        Player player = game.getPlayers().get(game.turnIndex);

        if (player instanceof HumanPlayer && player.handValue() < 17) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must reach 17 or higher before standing!");
            alert.showAndWait();
            return;
        }

        game.stand();
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

    // refresh game display and UI buttons
    private void refreshUI() {
        BlackjackGame game = BlackjackGame.getInstance();
        tableGrid.getChildren().clear();
        game.render(tableGrid);

        turnLabel.setText("Turn: " + game.getPlayers().get(game.turnIndex).getName());
        roundLabel.setText("Round: " + game.getRoundNumber());

        boolean isPlayerTurn = game.getPlayers().get(game.turnIndex) instanceof HumanPlayer;
        boolean roundOver = game.isRoundOver();
        int handValue = game.getPlayers().get(game.turnIndex).handValue();

        // Disable hit/stand based on rules
        boolean allowActions = isPlayerTurn && !roundOver && handValue < 21;
        hitButton.setDisable(!allowActions);
        standButton.setDisable(!allowActions || handValue < 17);

        // Highlight or blur disabled buttons (optional visual cue)
        hitButton.setOpacity(hitButton.isDisable() ? 0.4 : 1.0);
        standButton.setOpacity(standButton.isDisable() ? 0.4 : 1.0);

        // Status message
        String result = game.getLastRoundStatus();
        statusLabel.setText(result == null ? "" : result);
    }


    @FXML
    public void initialize() {
        refreshUI();
    }

    @FXML
    private void onNextRound() {
        BlackjackGame.getInstance().startNewRound();
        refreshUI();
    }
}
