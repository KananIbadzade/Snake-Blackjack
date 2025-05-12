package org.example.snakeblackjack.blackjack;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import org.example.snakeblackjack.HighScoreManager;

import java.util.*;

public class BlackjackGame {

    // Singleton pattern: only one instance of the game
    private static final BlackjackGame INSTANCE = new BlackjackGame();
    public static BlackjackGame getInstance() { return INSTANCE; }

    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();

    public int turnIndex = 0;
    private int roundNumber = 0;
    private boolean roundOver = false;
    private String lastRoundStatus = "";
    private BlackJackManager manager = new BlackJackManager();

    private String currentUserName;
    private HighScoreManager scoreManager;

    private final Map<Player, String> roundResults = new HashMap<>();
    private Runnable onRoundComplete;   // callback to update the UI

    public void setUsername(String username) {
        this.currentUserName = username;
    }

    public void setScoreManager(HighScoreManager manager) {
        this.scoreManager = manager;
    }


    public void updateScore(){
        if (scoreManager != null && currentUserName != null) {
            int oldScore = scoreManager.getBlackjackScore(currentUserName);
            System.out.println("OLD SCORE: " + oldScore);
            System.out.println("Current Score: " + roundNumber);

            if (oldScore == 1000 || roundNumber > oldScore) {
                scoreManager.updateBlackjackScore(currentUserName, roundNumber);
                System.out.println("BlackJack score updated to: " + roundNumber);
            }
        }
    }
    // refreshes screen after each round
    public void setOnRoundComplete(Runnable callback) {
        this.onRoundComplete = callback;
    }

    // private constructor initializes players
    private BlackjackGame() {
        players.add(new HumanPlayer("You"));
        players.add(new AutoPlayer("Bot A", 16));
        players.add(new AutoPlayer("Bot B", 18));
        players.add(new Dealer());
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public List<Player> getPlayers() {
        return players;
    }

    // checks balance, asks for bet, deals cards, etc
    public void startNewRound() {
        // show alert if you are broke
        if (players.get(0).getBalance() <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You're out of money!");
            alert.setContentText("Your balance is $0. The game is over.");
            alert.showAndWait();
            updateScore();
            return;
        }

        // reset deck if deck is running low
        if (deck.size() < 20) {
            deck.reset();
        }

        deck.shuffle();
        roundNumber++;
        roundOver = false;
        lastRoundStatus = "";
        roundResults.clear();

        // clear hands and place bets
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            p.clearHand();

            if (i == 0) {
                // User: ask for a valid bet
                int bet = 0;
                do {
                    bet = promptForBet(p);
                    if (bet <= 0 || bet > p.getBalance()) {
                        Alert invalid = new Alert(Alert.AlertType.WARNING);
                        invalid.setTitle("Invalid Bet");
                        invalid.setHeaderText("Please enter a valid bet.");
                        invalid.setContentText("Bet must be greater than 0 and not exceed your balance.");
                        invalid.showAndWait();
                    }
                } while (bet <= 0 || bet > p.getBalance());

                p.placeBet(bet);
            } else {
                // Bots bet $50 by default
                p.placeBet(50);
            }
        }

        // Deal 2 cards to each player
        turnIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (Player p : players) {
                p.take(deck.draw());
            }
        }

        // if player has Blackjack
        Player player = players.get(0);
        if (player.hasBlackjack()) {
            turnIndex = players.size(); // Skip to round end
            settleAllBets();
            roundOver = true;
            if (onRoundComplete != null) onRoundComplete.run();
        }
    }

    // dialog for bet input
    private int promptForBet(Player p) {
        while (true) {
            TextInputDialog betDialog = new TextInputDialog("50");
            betDialog.setHeaderText("Enter your bet amount:");
            betDialog.setContentText("Bet (must be > 0 and ≤ $" + p.getBalance() + "):");

            Optional<String> result = betDialog.showAndWait();
            if (result.isEmpty()) System.exit(0);  // Exit if user cancels

            try {
                int entered = Integer.parseInt(result.get().trim());
                if (entered > 0 && entered <= p.getBalance()) {
                    return entered;
                }
            } catch (NumberFormatException ignored) {}

            Alert invalid = new Alert(Alert.AlertType.WARNING);
            invalid.setTitle("Invalid Bet");
            invalid.setHeaderText("Invalid input!");
            invalid.setContentText("You must enter a number > 0 and not more than your balance.");
            invalid.showAndWait();
        }
    }

    // adds card to current player
    public void hit() {
        Player p = players.get(turnIndex);
        p.take(deck.draw());
        if (p.handValue() >= 21 || p.hasBlackjack()) {
            nextTurn();
        }
    }

    // ends current player’s turn
    public void stand() {
        nextTurn();
    }

    // moves to the next player's turn
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

        // if all are done, settles bets
        if (turnIndex >= players.size()) {
            settleAllBets();
            roundOver = true;
            if (onRoundComplete != null) onRoundComplete.run();
        }
    }

    // determines win/loss/push
    private void settleAllBets() {
        int dealerValue = players.get(3).handValue();
        StringBuilder status = new StringBuilder("Round Results:\n");
        status.append("Dealer: ").append(dealerValue).append("\n\n");

        for (int i = 0; i < 3; i++) {
            Player p = players.get(i);
            int val = p.handValue();

            if (val > 21) {
                p.loseBet();
                roundResults.put(p, "LOSES");
            } else if (p.hasBlackjack()) {
                p.blackjackWin();
                roundResults.put(p, "BLACKJACK");
            } else if (dealerValue > 21 || val > dealerValue) {
                p.winBet();
                roundResults.put(p, "WINS");
            } else if (val == dealerValue) {
                p.pushBet();
                roundResults.put(p, "PUSHES (tie)");
            } else {
                p.loseBet();
                roundResults.put(p, "LOSES");
            }

            status.append(p.getName()).append(": ")
                    .append(roundResults.get(p))
                    .append(" | Balance: $").append(p.getBalance())
                    .append("\n");
        }

        lastRoundStatus = status.toString();
    }

    // gets the card image filename based on its rank and suit
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

    // draws the cards and balances on the screen
    public void render(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < players.size(); row++) {
            Player p = players.get(row);

            // Show player name
            Label nameLabel = new Label(p.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            grid.add(nameLabel, 0, row);

            List<Card> hand = p.getHand();

            // hide dealer's second card if round not finished
            if (row == 3 && !roundOver) {
                if (hand.size() > 1) {
                    addCardToGrid(grid, hand.get(0), 1, row);  // visible card
                    addCardBack(grid, 2, row);                 // hidden card
                }
            } else {
                for (int col = 0; col < hand.size(); col++) {
                    addCardToGrid(grid, hand.get(col), col + 1, row);
                }
            }

            // showing balance + result
            if (row != 3) {
                String result = roundOver && roundResults.containsKey(p) ? " - " + roundResults.get(p) : "";
                Label balanceLabel = new Label("$" + p.getBalance() + result);
                String color = p.getBalance() > 0 ? "limegreen" : "red";
                balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
                grid.add(balanceLabel, hand.size() + 2, row);
            }
        }
    }

    // adds face-up card image to the grid
    private void addCardToGrid(GridPane grid, Card card, int col, int row) {
        String fileName = getCardImageFileName(card);
        Image img = new Image(getClass().getResourceAsStream("/images/cards/" + fileName));
        ImageView cardView = new ImageView(img);
        cardView.setFitWidth(80);
        cardView.setPreserveRatio(true);
        grid.add(cardView, col, row);
    }

    // adds face-down (hidden) card
    private void addCardBack(GridPane grid, int col, int row) {
        Image backImg = new Image(getClass().getResourceAsStream("/images/cards/BACK.png"));
        ImageView backView = new ImageView(backImg);
        backView.setFitWidth(80);
        backView.setPreserveRatio(true);
        grid.add(backView, col, row);
    }

    // Getters for other classes to access
    public int getRoundNumber() { return roundNumber; }

//    public String getLastRoundStatus() { return lastRoundStatus; }  // not used/needed

    public boolean isRoundOver() { return roundOver; }

    public String getSaveString() {

        return BlackJackManager.saveState(this);
    }

    public void loadFromString(String state) {
        BlackJackManager.loadState(this, state);
    }
}
