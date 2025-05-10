package org.example.snakeblackjack.blackjack;

import java.util.ArrayList;
import java.util.List;

/**
 *  participant in the blackjack game (player, bots, or dealer)
 */

public abstract class Player {
    protected final String name;

    // cards in the player’s hand
    protected final List<Card> hand = new ArrayList<>();


    protected int balance    = 1000;
    protected int currentBet = 0;


    public Player(String name) {
        this.name = name;
    }


    public int getBalance() {
        return balance;
    }


    public int getCurrentBet() {
        return currentBet;
    }


    public String getName() {
        return name;
    }


    public void placeBet(int amount) {
        currentBet = amount;
        balance -= amount;
    }


    public void winBet() {
        balance += 2 * currentBet;
        currentBet = 0;
    }


    public void pushBet() {
        balance += currentBet;
        currentBet = 0;
    }


    public void loseBet() {
        currentBet = 0;
    }


    public void clearHand() {
        hand.clear();
    }


    public void addCard(Card c) {
        hand.add(c);
    }


    public void take(Card c) {
        addCard(c);
    }


    public int handValue() {
        int total = 0;
        int aceCount = 0;

        // sum up all card values, counting Aces as 11 for now
        for (Card c : hand) {
            String rank = c.getRank();
            if (rank.equals("Jack") || rank.equals("Queen") || rank.equals("King")) {
                total += 10;
            } else if (rank.equals("Ace")) {
                total += 11;
                aceCount++;
            } else {
                // Numeric cards 2–10
                total += Integer.parseInt(rank);
            }
        }

        // if we bust and have some Aces counted as 11, convert them to 1
        while (total > 21 && aceCount > 0) {
            total -= 10;  // effectively making one Ace count as 1
            aceCount--;
        }

        return total;
    }

    /**
     * return a copy of the player’s current cards
     * (prevents outside code from accidentally modifying the hand list).
     */
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    /**
     * each subclass decides its own hit/stand logic:
     * – humanPlayer will wait for UI input
     * – autoPlayer and Dealer have their own thresholds
     * return true to take another card, false to stand
     */
    public abstract boolean wantsToHit();
}
