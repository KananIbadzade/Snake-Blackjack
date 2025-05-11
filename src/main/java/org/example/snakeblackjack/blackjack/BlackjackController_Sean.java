package org.example.snakeblackjack.blackjack;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class BlackjackController_Sean {

    //main containers
    public AnchorPane root;

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

    public enum PlayerType {we, auto1, auto2, dealer};
    private int coinPileCount = 0;
    @FXML
    public void initialize() {
        //hide all the status labels and coin piles
        gameStatus.setVisible(false);
        statusText1.setVisible(false);
        statusTextDealer.setVisible(false);
        statusText2.setVisible(false);
        statusTextWe.setVisible(false);

        coinPile1.setVisible(false);
        coinPile2.setVisible(false);
        coinPile3.setVisible(false);
        coinPile4.setVisible(false);
        coinPile5.setVisible(false);
        coinPile6.setVisible(false);
        coinPile7.setVisible(false);
    }

    // buttons related event handlers
    public void playerMainMenu(ActionEvent actionEvent) {

        actionEvent.consume();
    }

    public void playerLoadGame(ActionEvent actionEvent) {
        statusTextAnimation(gameStatus, "Load Game...", 1000);


        actionEvent.consume();
    }

    public void playerSaveGame(ActionEvent actionEvent) {
        statusTextAnimation(gameStatus, "Save Game...", 1000);


        actionEvent.consume();
    }

    public void playerStand(ActionEvent actionEvent) {
        statusTextAnimation(statusTextWe, "Stand!");

        actionEvent.consume();
    }

    public void playerDoubleDown(ActionEvent actionEvent) {
        statusTextAnimation(statusTextWe, "Double Down!");

        actionEvent.consume();
    }

    public void playerHit(ActionEvent actionEvent) {
        statusTextAnimation(statusTextWe, "Hit!");

        actionEvent.consume();
    }

    //helper method to shortly display a message on a label
    private void statusTextAnimation(Label label, String textToDisplay){
        statusTextAnimation(label, textToDisplay, 500); //default to 500 ms if not specified
    }

    private void statusTextAnimation(Label label, String textToDisplay, int timeMilli){
        label.setVisible(true);
        label.setText(textToDisplay);
        PauseTransition delay = new PauseTransition(Duration.millis(timeMilli));
        delay.setOnFinished(event -> label.setVisible(false));
        delay.play();
    }

    public void updateBandF(PlayerType player, int bet, int fund){
        switch(player){
            case we:
                fundValueWe.setText(String.valueOf(fund));
                betValueWe.setText(String.valueOf(bet));
                break;
            case auto1:
                fundValue1.setText(String.valueOf(fund));
                betValue1.setText(String.valueOf(bet));
                break;
            case auto2:
                fundValue2.setText(String.valueOf(fund));
                betValue2.setText(String.valueOf(bet));
                break;
            case dealer:
                fundValueDealer.setText(String.valueOf(fund));
                betValueDealer.setText(String.valueOf(bet));
                break;
            default:
                break;

        }
    }

    //called without a player specified -> clear all bets
    public void clearBet(){
        clearBet(PlayerType.we);
        clearBet(PlayerType.auto1);
        clearBet(PlayerType.auto2);
        clearBet(PlayerType.dealer);
    }
    public void clearBet(PlayerType player){
        switch(player){
            case we -> betValueWe.setText("");
            case auto1 -> betValue1.setText("");
            case auto2 -> betValue2.setText("");
            case dealer -> betValueDealer.setText("");
            default -> System.out.println("Invalid player type");
        }
    }
}
