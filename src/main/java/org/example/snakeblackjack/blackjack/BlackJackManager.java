package org.example.snakeblackjack.blackjack;

import java.util.List;

/**
 * responsible for saving and loading the game state
 * converts the entire game into a string, and back
 */
public class BlackJackManager {

    // Creates a string that stores all game data: turn, balances, bets, and hands
    public static String saveState(BlackjackGame game) {
        StringBuilder sb = new StringBuilder();

        // save the current turn
        sb.append(game.turnIndex).append("|");

        // saving name, balance, bet, and their hand
        List<Player> players = game.getPlayers();
        for (Player p : players) {
            StringBuilder handPart = new StringBuilder();

            // create list of card names for player
            List<Card> hand = p.getHand();
            for (int i = 0; i < hand.size(); i++) {
                handPart.append(hand.get(i).toString()); // ex: 7 of Spades
                if (i < hand.size() - 1) {
                    handPart.append(",");
                }
            }

            // format: name:balance:bet:card,card,card;
            sb.append(p.getName())
                    .append(":")
                    .append(p.getBalance())
                    .append(":")
                    .append(p.getCurrentBet())
                    .append(":")
                    .append(handPart)
                    .append(";");
        }

        return sb.toString();  // full encoded state
    }

    // loads a saved game string back into the game
    public static void loadState(BlackjackGame game, String state) {
        // splitting the save string into 2 parts: turnIndex and all players
        String[] parts = state.split("\\|", 2);
        game.turnIndex = Integer.parseInt(parts[0]);  // restoring which player's turn it is

        // get each player’s saved data
        String[] playerStrings = parts[1].split(";");
        List<Player> players = game.getPlayers();

        // going through each player and restore their info
        for (int i = 0; i < playerStrings.length && i < players.size(); i++) {
            String data = playerStrings[i];
            if (data.isEmpty()) continue;

            // format expected: name:balance:bet:card,card,...
            String[] fields = data.split(":", 4);
            Player p = players.get(i);

            // restore balance and current bet
            p.balance = Integer.parseInt(fields[1]);
            p.currentBet = Integer.parseInt(fields[2]);

            p.clearHand();

            // restore cards (if any were saved)
            String handField = fields[3];
            if (!handField.isEmpty()) {
                String[] cardStrings = handField.split(",");

                for (String cardStr : cardStrings) {
                    // each cardStr is like 7 of Spades
                    String[] rs = cardStr.split(" of ", 2);
                    String rank = rs[0];
                    String suit = rs[1];

                    // no need to store value, it is recalculated from rank
                    p.addCard(new Card(rank, suit));
                }
            }
        }
    }
}
