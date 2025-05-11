package org.example.snakeblackjack.blackjack;

// player controlled by the user. Decisions are made through the UI

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    // the UI will control hit/stand, so always return false here
    @Override
    public boolean wantsToHit() {
        return false;
    }
}
