package org.example.snakeblackjack.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Optional;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * handles all user interactions and updates the UI
 * tied to the FXML layout with @FXML annotations.
 */
public class BlackjackController {

    // FXML UI elements (linked by ID from the .fxml file)
    @FXML private GridPane tableGrid;
    @FXML private Label turnLabel;
    @FXML private Label roundLabel;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    private MediaPlayer mediaPlayer;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    // Called when the controller is initialized (auto-run by JavaFX)
    @FXML
    public void initialize() {
        String musicFile = "/audio/casinoMusic.mp3"; // relative to resources/
        Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop music
        mediaPlayer.play();

        BlackjackGame game = BlackjackGame.getInstance();
        game.setOnRoundComplete(() -> refreshUI());
        refreshUI();
    }

    // "Hit" button is clicked
    @FXML
    private void onHit() {
        if (hitButton.isDisabled()) return;
        BlackjackGame.getInstance().hit();
        refreshUI();
    }

    // "Stand" button is clicked.
    @FXML
    private void onStand() {
        if (standButton.isDisabled()) return;

        BlackjackGame game = BlackjackGame.getInstance();
        Player player = game.getPlayers().get(game.turnIndex);

        // stand if < 17
        if (player instanceof HumanPlayer && player.handValue() < 17) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must reach 17 or higher before standing!");
            alert.showAndWait();
            return;
        }
        game.stand();
        refreshUI();
    }

    // "Save" button is clicked
    @FXML
    private void onSaveState() {
        String save = BlackjackGame.getInstance().getSaveString();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Your save string (copy this):");
        alert.setContentText(save);

        DialogPane dialogPane = alert.getDialogPane();
        TextArea textArea = new TextArea(alert.getContentText());
        textArea.setEditable(false);  // Make it read-only
        textArea.setWrapText(true);   // Enable text wrapping
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        dialogPane.setContent(textArea);
        alert.showAndWait();
    }

    // "Load" button is clicked
    @FXML
    private void onLoadState() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Paste your save string here:");
        dialog.setContentText("Save data:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(saveString -> {
            BlackjackGame.getInstance().loadFromString(saveString);
            refreshUI();
        });
    }

    // "Main Menu" button is clicked
    @FXML
    private void backToMainMenu() {
        Stage stage = (Stage) tableGrid.getScene().getWindow();
        mediaPlayer.stop();
        stage.close();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainmenu.fxml"));
            root = loader.load();
        }catch (Exception e) { System.out.println("Error loading main menu");}
        stage.setTitle("MainMenu");
        this.stage.setScene(new Scene(root));
        this.stage.setResizable(false);
        this.stage.show();
    }

    // "Next Round" button is clicked
    @FXML
    private void onNextRound() {
        BlackjackGame.getInstance().startNewRound();
        refreshUI();
    }

    // updates the screen: cards, turn, round number, and button states
    private void refreshUI() {
        BlackjackGame game = BlackjackGame.getInstance();

        tableGrid.getChildren().clear();
        game.render(tableGrid);

        boolean isRoundOver = game.isRoundOver();

        // whose turn it is
        int currentTurn = game.turnIndex;
        boolean isHumanTurn = currentTurn < game.getPlayers().size() && game.getPlayers().get(currentTurn) instanceof HumanPlayer;

        // get hand value only if it's the human's turn
        int playerHandValue = isHumanTurn ? game.getPlayers().get(currentTurn).handValue() : 0;

        // updating the labels showing whose turn and which round
        if (isHumanTurn) {
            turnLabel.setText("Turn: " + game.getPlayers().get(currentTurn).getName());
        } else {
            turnLabel.setText("Turn: —");
        }
        roundLabel.setText("Round: " + game.getRoundNumber());

        // when player is allowed to click buttons
        boolean canAct = isHumanTurn && !isRoundOver && playerHandValue < 21;

        // enable/disable buttons
        hitButton.setDisable(isRoundOver || !canAct);
        standButton.setDisable(isRoundOver || !canAct || playerHandValue < 17);

        // fading buttons if disabled
        hitButton.setOpacity(hitButton.isDisable() ? 0.4 : 1.0);
        standButton.setOpacity(standButton.isDisable() ? 0.4 : 1.0);
    }
}
