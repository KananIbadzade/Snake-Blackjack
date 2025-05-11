package org.example.snakeblackjack.blackjack;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlackjackGame {

    private static final BlackjackGame INSTANCE = new BlackjackGame();
    public static BlackjackGame getInstance() { return INSTANCE; }

    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();
    public int turnIndex = 0;

    private int roundNumber = 0;
    private String lastRoundStatus = "";
    private boolean roundOver = false;

    private BlackjackGame() {
        players.add(new HumanPlayer("You"));
        players.add(new AutoPlayer("Bot A", 16));
        players.add(new AutoPlayer("Bot B", 18));
        players.add(new Dealer());
        startNewRound();
    }

    public List<Player> getPlayers() { return players; }

    public void startNewRound() {
        deck.shuffle();
        roundNumber++;
        lastRoundStatus = "";
        roundOver = false;

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            p.clearHand();

            if (i == 0) {
                int bet = promptForBet(p);
                p.placeBet(bet);
            } else {
                p.placeBet(50);
            }
        }

        turnIndex = 0;

        for (int i = 0; i < 2; i++) {
            for (Player p : players) {
                p.take(deck.draw());
            }
        }
    }

    private int promptForBet(Player p) {
        TextInputDialog betDialog = new TextInputDialog("50");
        betDialog.setHeaderText("Enter your bet amount:");
        betDialog.setContentText("Bet:");
        Optional<String> result = betDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int entered = Integer.parseInt(result.get());
                if (entered > 0 && entered <= p.getBalance()) {
                    return entered;
                }
            } catch (NumberFormatException ignored) {}
        }
        return 50;
    }

    public void hit() {
        Player p = players.get(turnIndex);
        p.take(deck.draw());
        if (p.handValue() >= 21) {
            nextTurn();
        }
    }

    public void stand() {
        nextTurn();
    }

    private void nextTurn() {
        turnIndex++;

        while (turnIndex < players.size()) {
            Player current = players.get(turnIndex);
            if (!(current instanceof HumanPlayer)) {
                while (current.wantsToHit()) {
                    current.take(deck.draw());
                    if (current.handValue() >= 21) break;
                }
                turnIndex++;
            } else {
                break;
            }
        }

        if (turnIndex >= players.size()) {
            settleAllBets();
            roundOver = true;
        }
    }

    private void settleAllBets() {
        int dealerValue = players.get(3).handValue();
        StringBuilder status = new StringBuilder("Round Results:\n");
        status.append("Dealer: ").append(dealerValue).append("\n\n");

        for (int i = 0; i < 3; i++) {
            Player p = players.get(i);
            int val = p.handValue();
            int oldBalance = p.getBalance();
            int bet = p.getCurrentBet();

            if (val > 21) {
                p.loseBet();
            } else if (val == 21 && p.getHand().size() == 2) {
                int bonus = (int)(bet * 1.5);
                p.balance += bonus + bet;
                p.currentBet = 0;
            } else if (dealerValue > 21) {
                p.winBet();
            } else if (val > dealerValue) {
                p.winBet();
            } else if (val == dealerValue) {
                p.pushBet();
            } else {
                p.loseBet();
            }

            int newBalance = p.getBalance();
            status.append(p.getName()).append(" (Score: ").append(val).append(", Balance: $").append(newBalance).append(") - ");
            if (newBalance > oldBalance) {
                status.append("WINS\n");
            } else if (newBalance < oldBalance) {
                status.append("LOSES\n");
            } else {
                status.append("PUSHES (tie)\n");
            }
        }

        lastRoundStatus = status.toString();
    }

    private String getCardImageFileName(Card card) {
        String rank = card.getRank();
        String suit = card.getSuit();
        String suitLetter = switch (suit.toLowerCase()) {
            case "spades" -> "S";
            case "clubs" -> "C";
            case "hearts" -> "H";
            case "diamonds" -> "D";
            default -> "";
        };

        String shortRank = switch (rank.toLowerCase()) {
            case "jack" -> "J";
            case "queen" -> "Q";
            case "king" -> "K";
            case "ace" -> "A";
            default -> rank;
        };
        return shortRank + "-" + suitLetter + ".png";
    }

    public void render(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < players.size(); row++) {
            Player p = players.get(row);

            Label nameLabel = new Label(p.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            grid.add(nameLabel, 0, row);

            List<Card> hand = p.getHand();

            if (row == 3 && !roundOver) {
                if (hand.size() > 1) {
                    Card visibleCard = hand.get(0);
                    String fileName = getCardImageFileName(visibleCard);
                    Image faceUp = new Image(getClass().getResourceAsStream("/images/cards/" + fileName));
                    ImageView frontView = new ImageView(faceUp);
                    frontView.setFitWidth(80);
                    frontView.setPreserveRatio(true);
                    grid.add(frontView, 1, row);

                    Image backImg = new Image(getClass().getResourceAsStream("/images/cards/00_BACK.png"));
                    ImageView backView = new ImageView(backImg);
                    backView.setFitWidth(80);
                    backView.setPreserveRatio(true);
                    grid.add(backView, 2, row);
                }
            } else {
                for (int col = 0; col < hand.size(); col++) {
                    Card card = hand.get(col);
                    String fileName = getCardImageFileName(card);
                    Image img = new Image(getClass().getResourceAsStream("/images/cards/" + fileName));
                    ImageView cardView = new ImageView(img);
                    cardView.setFitWidth(80);
                    cardView.setPreserveRatio(true);
                    grid.add(cardView, col + 1, row);
                }
            }

            Label balanceLabel = new Label("$" + p.getBalance());
            String color = p.getBalance() > 0 ? "limegreen" : "red";
            balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
            grid.add(balanceLabel, hand.size() + 2, row);
        }
    }

    public String getSaveString() {
        return BlackJackManager.saveState(this);
    }

    public void loadFromString(String state) {
        BlackJackManager.loadState(this, state);
    }

    public int getRoundNumber() { return roundNumber; }
    public String getLastRoundStatus() { return lastRoundStatus; }
    public boolean isRoundOver() { return roundOver; }
}
