package org.example.snakeblackjack.blackjack;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BlackjackController_Sean {

    //main containers
    public Stage blackJackStage;
    public Scene blackJackScene;
    public AnchorPane blkJkAnchor;

    //coin piles
    public Pane coinPilesPane;
    public ImageView coinPile1;
    public ImageView coinPile2;
    public ImageView coinPile3;
    public ImageView coinPile4;
    public ImageView coinPile5;
    public ImageView coinPile6;
    public ImageView coinPile7;

    //player information containers
    //Auto1
    public Pane valuePane1;
    public Rectangle valueAuto1;
    public Label fundValue1;
    public Label betValue1;
    public Label statusText1;
    public Label handValue1;

    //Dealer
    public Pane valuePaneDealer;
    public Rectangle valueAutoDealer;
    public Label fundValueDealer;
    public Label betValueDealer;
    public Label statusTextDealer;
    public Label handValueDealer;

    //Auto2
    public Pane valuePane2;
    public Rectangle valueAuto2;
    public Label fundValue2;
    public Label betValue2;
    public Label statusText2;
    public Label handValue2;

    //We
    public Pane valuePaneWe;
    public Rectangle valueWe;
    public Label fundValueWe;
    public Label betValueWe;
    public Label statusTextWe;
    public Label handValueWe;

    //initial cards
    public ImageView initCardAuto11;
    public ImageView initCardAuto12;
    public ImageView initCardDealer1;
    public ImageView initCardDealer2;
    public ImageView initCardAuto22;
    public ImageView initCardAuto21;
    public ImageView initCardWe1;
    public ImageView initCardWe2;

    //game related
    public Label gameStatus;
    public Label roundLabel;
    public Button hitButton;
    public Button doubleDownButton;
    public Button standButton;
    public Label turnLabel;
    public AnchorPane root;


    @FXML
    public void initialize() {}
}
