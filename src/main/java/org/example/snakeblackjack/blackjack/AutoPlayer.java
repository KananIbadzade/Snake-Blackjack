package org.example.snakeblackjack.blackjack;

// bot player that keeps hitting until it reaches a specific threshold.

public class AutoPlayer extends Player {
    private final int hold;

    public AutoPlayer(String name, int hold) {
        super(name);
        this.hold = hold;
    }

    @Override
    public boolean wantsToHit() {
        return handValue() < hold;
    }
}
