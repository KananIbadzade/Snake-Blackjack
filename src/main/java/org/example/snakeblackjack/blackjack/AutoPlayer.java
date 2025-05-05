package org.example.snakeblackjack.blackjack;

// an automated player that keeps hitting until reaching a set threshold
public class AutoPlayer extends Player {
    private final int threshold;

    public AutoPlayer(String name, int threshold) {
        super(name);
        this.threshold = threshold;
    }

    @Override
    public boolean wantsToHit() {
        int total = handValue();
        // if our total is less than the threshold, we hit
        if (total < threshold) {
            return true;
        }
        // otherwise we stand
        return false;
    }
}
