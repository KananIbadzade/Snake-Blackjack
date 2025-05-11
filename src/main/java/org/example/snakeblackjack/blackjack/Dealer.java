package org.example.snakeblackjack.blackjack;

/**
 * Dealer follows standard blackjack rules.
 * Hits below 17 or on a “soft” 17 (with Ace).
 */
public class Dealer extends Player {

    public Dealer() {
        super("Dealer");
    }

    @Override
    public boolean wantsToHit() {
        int total = handValue();

        // checks if dealer has an Ace (for soft 17 rule)
        boolean hasAce = false;
        for (Card c : hand) {
            if (c.getRank().equals("Ace")) {
                hasAce = true;
                break;
            }
        }

        return total < 17 || (total == 17 && hasAce);
    }
}
