package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class BlackJack extends Application {

    public static void launchGame(Stage stage) {
        try{new BlackJack().start(stage);}
        catch (IOException e){}// start the JavaFX lifecycle
    }

    @Override
    public void start(Stage stage) throws IOException {

        Scene preScene = getPreScene();
        Scene gameScene = getGameScene();

        /* commented out setting the icon code May.01.25
        InputStream input = getClass().getResourceAsStream("/icon.png");
        Image icon = new Image(input);

        stage.getIcons().add(icon); */
        stage.setTitle("Best Black Jack Game in the World");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setScene(preScene);


        preScene.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.SPACE)) {
                event.consume();
                stage.setScene(gameScene);
            }
        });


        stage.show();

    }

    private Scene getGameScene() {
        Group root = new Group();
        return new Scene(root);
    }

    private Scene getPreScene() {
        Group root = new Group();
        return new Scene(root);
    }
}
