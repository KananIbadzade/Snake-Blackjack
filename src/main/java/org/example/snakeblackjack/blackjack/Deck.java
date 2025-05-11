package org.example.snakeblackjack.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a deck of cards used in Blackjack
 * Supports shuffling, drawing, and resetting
 */
public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        reset();
    }

    // converts card names to values
    private int lookupValue(String rank) {
        switch (rank) {
            case "Jack":
            case "Queen":
            case "King":
                return 10;
            case "Ace":
                return 11;
            default:
                return Integer.parseInt(rank); // 2–10
        }
    }

    // Builds a full 52-card deck and shuffles it, called automatically when the game starts or deck is low
    public void reset() {
        cards.clear();

        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] ranks = {
                "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "Jack", "Queen", "King", "Ace"
        };

        for (String suit : suits) {
            for (String rank : ranks) {
                int value = lookupValue(rank);
                cards.add(new Card(rank, suit));
            }
        }

        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * draws and removes the top card from the deck
     * throws error if deck is empty
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Deck is empty");
        }
        return cards.remove(0);
    }

    // how many cards left in the deck
    public int size() {
        return cards.size();
    }
}
