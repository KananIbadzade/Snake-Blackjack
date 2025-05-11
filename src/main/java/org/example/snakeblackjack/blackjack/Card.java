package org.example.snakeblackjack.blackjack;

public class Card {
    private final String rank;     // 2, 10, King, Ace
    private final String symbol;   // Hearts, Spades

    public Card(String rank, String symbol) {
        this.rank = rank;
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return symbol;
    }

    @Override
    public String toString() {
        return rank + " of " + symbol;
    }
}
