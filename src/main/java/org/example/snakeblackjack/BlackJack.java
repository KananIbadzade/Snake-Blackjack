package org.example.snakeblackjack;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class BlackJack extends Application {

    public static void launchGame(Stage stage) {
        try{new BlackJack().start(stage);}
        catch (IOException e){ System.out.println(e.getMessage());}// start the JavaFX lifecycle
    }

    @Override
    public void start(Stage stage) throws IOException {

        Scene preScene = getPreScene(); // card drawing animation scene
        Scene gameScene = getGameScene();
        /*
        Image image = new Image("https://drive.google.com/uc?export=view&id=1xU9nFe8FyrNftQp4XvT5COUXCoKvDuBB");
        stage.getIcons().add(image); */

        stage.setTitle("Black Jack He He");
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
