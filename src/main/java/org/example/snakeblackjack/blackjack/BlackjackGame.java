package org.example.snakeblackjack.blackjack;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * BlackjackGame -> game logic:
 *  -deals cards,
 *  -tracks whose turn it is,
 *  -handles hit/stand actions,
 *  -settles bets at the end of each round,
 *  -can render the state into a GridPane,
 *  -and can save or load its entire state via strings.
 */
public class BlackjackGame {

    // singleton instance so everyone uses the same game
    private static final BlackjackGame INSTANCE = new BlackjackGame();
    public static BlackjackGame getInstance() {
        return INSTANCE;
    }

    private final Deck deck = new Deck();

    // list of the 4 players: Player, Bot A, Bot B, Dealer
    private final List<Player> players = new ArrayList<>();

    // which player’s turn it is (0 = User, 1 = Bot A, 2 = Bot B, 3 = Dealer)
    int turnIndex = 0;

    // private constructor sets up players and starts round 1
    private BlackjackGame() {
        players.add(new HumanPlayer("You"));
        players.add(new AutoPlayer("Bot A", 16));
        players.add(new AutoPlayer("Bot B", 18));
        players.add(new Dealer());
        startNewRound();
    }


    public List<Player> getPlayers() {
        return players;
    }


    public void startNewRound() {
        deck.shuffle();

        for (Player p : players) {
            p.clearHand();
            p.placeBet(50);
        }

        // start with player #0
        turnIndex = 0;

        // deal two cards to everyone
        for (int i = 0; i < 2; i++) {
            for (Player p : players) {
                p.take(deck.draw());
            }
        }
    }

    // called when the human player clicks “Hit”
    public void hit() {
        Player p = players.get(turnIndex);
        p.take(deck.draw());
        // if they bust, move on immediately
        if (p.handValue() > 21) {
            nextTurn();
        }
    }

    // called when the human player clicks “Stand”
    public void stand() {
        nextTurn();
    }

    // move to the next player (or settle and reset if we’ve gone past the dealer)
    private void nextTurn() {
        turnIndex++;
        if (turnIndex >= players.size()) {
            settleAllBets();
            startNewRound();
        }
    }

    // compare each non-dealer to dealer and pay out or collect bets
    private void settleAllBets() {
        int dealerValue = players.get(3).handValue();

        // for each of the first 3 players
        for (int i = 0; i < 3; i++) {
            Player p = players.get(i);
            int val = p.handValue();

            if (val > 21) {
                p.loseBet();       // bust => lose
            }
            else if (dealerValue > 21) {
                p.winBet();        // dealer busts => win
            }
            else if (val > dealerValue) {
                p.winBet();        // higher than dealer => win
            }
            else if (val == dealerValue) {
                p.pushBet();       // tie => push
            }
            else {
                p.loseBet();       // lower => lose
            }
        }
    }

    //logic for render() method
    private String getCardImageFileName(Card card) {
        String rank = card.getRank(); // e.g., "2", "10", "King"
        String suit = card.getSuit(); // e.g., "Spades", "Clubs"

        // Convert full suit name to single letter
        String suitLetter = switch (suit.toLowerCase()) {
            case "spades" -> "S";
            case "clubs" -> "C";
            case "hearts" -> "H";
            case "diamonds" -> "D";
            default -> "";
        };

        // Convert rank to short form if needed
        String shortRank = switch (rank.toLowerCase()) {
            case "jack" -> "J";
            case "queen" -> "Q";
            case "king" -> "K";
            case "ace" -> "A";
            default -> rank; // numbers like "2", "3", ..., "10"
        };

        return shortRank + "-" + suitLetter + ".png";
    }


    /**
     * Draw the current state into a GridPane.
     * (Will add Labels or ImageViews to show names, cards, and balances)
     */
    public void render(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < players.size(); row++) {
            Player p = players.get(row);

            // 1) show the player’s name in column 0
            Label nameLabel = new Label(p.getName());
            grid.add(nameLabel, 0, row);

            // 2) show each card next to the name
            List<Card> hand = p.getHand();
            for (int col = 0; col < hand.size(); col++) {
                Card c = hand.get(col);
                // simply display “Rank of Suit”
                String fileName = getCardImageFileName(c);
                Image cardImg = new Image(getClass().getResourceAsStream("/images/cards/" + fileName));
                ImageView cardView = new ImageView(cardImg);
                cardView.setFitWidth(80);
                cardView.setPreserveRatio(true);
                grid.add(cardView, col + 1, row);
            }

            // 3) show the balance after the cards
            Label balanceLabel = new Label("$" + p.getBalance());
            // put it two columns after the last card
            grid.add(balanceLabel, hand.size() + 1, row);
        }
    }

    // hand off to the manager to produce a save string
    public String getSaveString() {
        return BlackJackManager.saveState(this);
    }

    // hand off to the manager to restore from a save string
    public void loadFromString(String state) {
        BlackJackManager.loadState(this, state);
    }
}
