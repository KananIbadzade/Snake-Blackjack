package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.net.URL;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        URL resourceUrl = getClass().getResource("/org/example/snakeblackjack/mainmenu.fxml");
        System.out.println("Resource URL: " + resourceUrl);

        if (resourceUrl == null) {
            throw new FileNotFoundException("FXML file not found!");
        }
        // load the FXML for the menu
        Parent root = FXMLLoader.load(resourceUrl);
        stage.setTitle("Game Manager");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
