package org.example.snakeblackjack.blackjack;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class BlackjackGame {
    // singleton instance
    private static final BlackjackGame INSTANCE = new BlackjackGame();
    public static BlackjackGame getInstance() {
        return INSTANCE;
    }

    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();

    // renamed from 'currentPlayer'; package-private so BlackJackManager can access directly
    int turnIndex = 0;

    private BlackjackGame() {
        // setup players
        players.add(new HumanPlayer("You"));
        players.add(new AutoPlayer("Bot A", 16));
        players.add(new AutoPlayer("Bot B", 18));
        players.add(new Dealer());

        // start first round
        newRound();
    }

    /** Expose the player list to the manager */
    public List<Player> getPlayers() {
        return players;
    }

    /** Start a fresh round: shuffle, clear hands, place bets, deal two cards each */
    public void newRound() {
        deck.shuffle();
        // example fixed bet of 50
        players.forEach(p -> {
            p.clearHand();
            p.placeBet(50);
        });
        turnIndex = 0;
        for (int i = 0; i < 2; i++) {
            players.forEach(p -> p.take(deck.draw()));
        }
    }

    /** Human “Hit” action */
    public void hit() {
        Player p = players.get(turnIndex);
        p.take(deck.draw());
        if (p.handValue() > 21) {
            nextTurn();
        }
    }

    /** Human “Stand” action */
    public void stand() {
        nextTurn();
    }

    /** Advance to next player, or settle and start a new round */
    private void nextTurn() {
        turnIndex++;
        if (turnIndex >= players.size()) {
            settleBets();
            newRound();
        }
    }

    /** Compare each non-dealer to dealer, pay out or collect bets */
    private void settleBets() {
        int dealerVal = players.get(3).handValue();
        for (int i = 0; i < players.size() - 1; i++) {
            Player p = players.get(i);
            int value = p.handValue();
            if (value > 21)              p.loseBet();
            else if (dealerVal > 21)     p.winBet();
            else if (value > dealerVal)  p.winBet();
            else if (value == dealerVal) p.pushBet();
            else                          p.loseBet();
        }
    }

    /**
     * Render each player’s name, hand and balance into the supplied GridPane.
     * (Fill in with your actual JavaFX node-creation code.)
     */
    public void render(GridPane grid) {
        grid.getChildren().clear();
        for (int row = 0; row < players.size(); row++) {
            Player p = players.get(row);
            // e.g.:
            // Label name = new Label(p.getName());
            // grid.add(name, 0, row);
            //
            // for (int col = 0; col < p.getHand().size(); col++) {
            //     Card c = p.getHand().get(col);
            //     Label card = new Label(c.toString());
            //     grid.add(card, col + 1, row);
            // }
            //
            // Label money = new Label("$" + p.getBalance());
            // grid.add(money, p.getHand().size() + 2, row);
        }
    }

    /** Delegate to your save/load manager */
    public String getSaveString() {
        return BlackJackManager.saveState(this);
    }

    public void loadFromString(String state) {
        BlackJackManager.loadState(this, state);
    }
}
