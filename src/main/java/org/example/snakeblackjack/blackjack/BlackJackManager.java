package org.example.snakeblackjack.blackjack;

import java.util.List;

public class BlackJackManager {

    // encode the entire game state as one long string
    public static String saveState(BlackjackGame game) {
        StringBuilder sb = new StringBuilder();

        // first, store whose turn it is
        sb.append(game.turnIndex).append("|");

        // then, for each player, store name:balance:bet:card,card,card;
        List<Player> players = game.getPlayers();
        for (Player p : players) {
            // build the comma-separated hand
            StringBuilder handPart = new StringBuilder();
            List<Card> hand = p.getHand();
            for (int i = 0; i < hand.size(); i++) {
                handPart.append(hand.get(i).toString());
                if (i < hand.size() - 1) {
                    handPart.append(",");
                }
            }

            // append name, balance, currentBet, and hand
            sb.append(p.getName())
                    .append(":")
                    .append(p.getBalance())
                    .append(":")
                    .append(p.getCurrentBet())
                    .append(":")
                    .append(handPart)
                    .append(";");
        }

        return sb.toString();
    }

    // parse the string and restore into the given game object
    public static void loadState(BlackjackGame game, String state) {
        // split off the turnIndex
        String[] parts = state.split("\\|", 2);
        game.turnIndex = Integer.parseInt(parts[0]);

        // now handle each player's data
        String[] playerStrings = parts[1].split(";");
        List<Player> players = game.getPlayers();

        for (int i = 0; i < playerStrings.length && i < players.size(); i++) {
            String data = playerStrings[i];
            if (data.isEmpty()) continue;

            // data format is name:balance:bet:card,card,card
            String[] fields = data.split(":", 4);
            Player p = players.get(i);

            // restore balance and bet
            p.balance    = Integer.parseInt(fields[1]);
            p.currentBet = Integer.parseInt(fields[2]);

            // clear old hand
            p.clearHand();

            // restore cards if any
            String handField = fields[3];
            if (!handField.isEmpty()) {
                String[] cardStrings = handField.split(",");
                for (String cardStr : cardStrings) {
                    // cardStr is "Rank of Suit"
                    String[] rs = cardStr.split(" of ", 2);
                    String rank = rs[0];
                    String suit = rs[1];
                    // use 0 for value — the game logic will recalc via rank/suit
                    p.addCard(new Card(rank, suit, 0));
                }
            }
        }
    }
}
