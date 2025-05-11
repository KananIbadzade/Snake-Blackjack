package org.example.snakeblackjack;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private AccountManager accountManager = new AccountManager();
    private HighScoreManager scoreManager = new HighScoreManager();

    public static String loggedInUserName;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (accountManager.checkExist(username, password)) {
            loggedInUserName = username;
            //scoreManager.defaultScoresForUsers(username);
            statusLabel.setText("Login successful!");
            openGameManager();
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    public void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (accountManager.createAccount(username, password)) {
            statusLabel.setText("Account created!");
        } else {
            statusLabel.setText("Username already exists.");
        }
    }

    private void openGameManager() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainmenu.fxml"));
            Parent root = loader.load();
//            Parent root = FXMLLoader.load(getClass().getResource("/mainmenu.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Something went wrong!");
        }
    }
}

