package org.example.snakeblackjack.blackjack;

public class Card {
    private final String rank;
    private final String symbol ;
    private final int value;

    public Card(String rank, String symbol, int value) {
        this.rank = rank;
        this.symbol  = symbol;
        this.value = value;
    }
    public String getRank() {
        return rank;
    }
    public String getSuit() {
        return symbol;
    }
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return rank + " of " + symbol ;
    }
}
