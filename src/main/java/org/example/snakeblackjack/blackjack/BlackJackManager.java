package org.example.snakeblackjack.blackjack;

import java.util.stream.Collectors;

public class BlackJackManager {
    /** Encode players’ hands, balances, bets, and turnIndex */
    public static String saveState(BlackjackGame game) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.turnIndex).append("|");
        for (Player p : game.getPlayers()) {
            String hand = p.hand.stream()
                    .map(Card::toString)
                    .collect(Collectors.joining(","));
            sb.append(p.getName()).append(":")
                    .append(p.getBalance()).append(":")
                    .append(p.getCurrentBet()).append(":")
                    .append(hand).append(";");
        }
        return sb.toString();
    }

    /** Parse and restore into game */
    public static void loadState(BlackjackGame game, String state) {
        String[] parts = state.split("\\|",2);
        game.turnIndex = Integer.parseInt(parts[0]);
        String[] playersData = parts[1].split(";");
        for (int i = 0; i < playersData.length; i++) {
            String[] f = playersData[i].split(":");
            Player p = game.getPlayers().get(i);
            p.balance = Integer.parseInt(f[1]);
            p.currentBet = Integer.parseInt(f[2]);
            p.clearHand();
            if (!f[3].isEmpty()) {
                for (String cardStr : f[3].split(",")) {
                    // cardStr format = "Rank of Suit"
                    String[] rs = cardStr.split(" of ");
                    // value lookup omitted for brevity
                    p.addCard(new Card(rs[0], rs[1], 0));
                }
            }
        }
    }
}
