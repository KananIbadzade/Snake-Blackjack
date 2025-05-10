package org.example.snakeblackjack.blackjack;

// a human-controlled player — UI buttons determine hit/stand
public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public boolean wantsToHit() {
        return false;
    }
}
