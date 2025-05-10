package org.example.snakeblackjack.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] ranks = {
                "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "Jack", "Queen", "King", "Ace"
        };

        for (String suit : suits) {
            for (String rank : ranks) {
                int value = lookupValue(rank);
                cards.add(new Card(rank, suit, value));
            }
        }

        shuffle();
    }


    private int lookupValue(String rank) {
        switch (rank) {
            case "Jack":
            case "Queen":
            case "King":
                return 10;
            case "Ace":
                return 11;
            default:
                return Integer.parseInt(rank);
        }
    }

    // randomizing the order of the cards
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * remove and return the top card.
     * @throws NoSuchElementException if there are no cards left.
     */

    public Card draw() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Deck is empty");
        }
        return cards.remove(0);
    }
}
