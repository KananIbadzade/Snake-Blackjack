package org.example.snakeblackjack.blackjack;

public class Dealer extends Player {
    public Dealer() {
        super("Dealer");
    }

    @Override
    public boolean wantsToHit() {
        int total = handValue();

        // check if there's an Ace in the hand
        boolean hasAce = false;
        for (Card c : hand) {
            if (c.getRank().equals("Ace")) {
                hasAce = true;
                break;
            }
        }

        // hit on any total less than 17
        // or on a “soft” 17 (total == 17 and at least one Ace)
        if (total < 17) {
            return true;
        }
        if (total == 17 && hasAce) {
            return true;
        }
        return false;  // otherwise stand
    }
}
