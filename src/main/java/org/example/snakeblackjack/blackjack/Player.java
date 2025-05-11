package org.example.snakeblackjack.blackjack;

import java.util.ArrayList;
import java.util.List;

// Base class for all players in the game (human, bot, dealer)

public abstract class Player {
    protected final String name;
    protected final List<Card> hand = new ArrayList<>();

    protected int balance = 1000;
    protected int currentBet = 0;

    public Player(String name) {
        this.name = name;
    }

    // --- Getters ---

    public int getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public String getName() {
        return name;
    }

    // return a copy of the hand to avoid leaks
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    //***** betting Logic *****

    public void placeBet(int amount) {
        currentBet = amount;
        balance -= amount;
    }

    public void winBet() {
        balance += 2 * currentBet;
        currentBet = 0;
    }

    public void blackjackWin() {
        balance += currentBet + (int)(currentBet * 1.5);
        currentBet = 0;
    }

    // tie
    public void pushBet() {
        balance += currentBet;
        currentBet = 0;
    }

    public void loseBet() {
        currentBet = 0;
    }

    // ***** card logic *****

    public void clearHand() {
        hand.clear();
    }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void take(Card c) {
        addCard(c);
    }

    // calculating the hand (Ace = 11 unless it busts)
    public int handValue() {
        int total = 0;
        int aceCount = 0;

        for (Card c : hand) {
            String rank = c.getRank();
            if (rank.equals("Jack") || rank.equals("Queen") || rank.equals("King")) {
                total += 10;
            } else if (rank.equals("Ace")) {
                total += 11;
                aceCount++;
            } else {
                total += Integer.parseInt(rank); // cards 2–10
            }
        }

        // if Aces exits and total > 21, we recalculate as Ace = 1
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    // checking blackjack on the first hand
    public boolean hasBlackjack() {
        return hand.size() == 2 && handValue() == 21;
    }

    // defines when to hit for each player
    public abstract boolean wantsToHit();
}
