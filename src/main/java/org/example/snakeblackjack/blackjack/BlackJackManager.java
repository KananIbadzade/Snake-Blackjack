package org.example.snakeblackjack.blackjack;

import javax.crypto.SecretKey;
import java.util.Base64; // Needed for IV handling later
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * responsible for saving and loading the game state
 * converts the entire game into a string, and back
 */
public class BlackJackManager {
    private static final Logger LOGGER = Logger.getLogger(BlackJackManager.class.getName());
    public static SecretKey key;

    static {
        SecretKey tempKey = null;
        try {
            tempKey = AESEncryption.generateKey();
            LOGGER.info("Static encryption KEY generated successfully for BlackJackManager class.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "FATAL: Failed to initialize static encryption KEY for BlackJackManager", e);
            throw new ExceptionInInitializerError("Failed to initialize static encryption KEY: " + e.getMessage() + e);
        }
        key = tempKey;
    }

    public BlackJackManager() {
        LOGGER.info("BlackJackManager instance created (if needed). Static KEY should be available.");
    }


    // Saves IV and Cypher Text so we can Decode later
    private static String combineIvAndCiphertext(byte[] iv, String ciphertext) {
        return Base64.getEncoder().encodeToString(iv) + "!" + ciphertext; // Using "!" as a delimiter
    }

    // Method to split IV and ciphertext
    private static String[] splitIvAndCiphertext(String combined) {
        return combined.split("!", 2);
    }

    // Creates a string that stores all game data: turn, balances, bets, and hands
    public static String saveState(BlackjackGame game) {
        StringBuilder sb = new StringBuilder();

        // save the current turn + delimiter
        sb.append(game.turnIndex).append("|");

        // Getting Player Objects
        // List<Player>
        List<Player> players = game.getPlayers();
        for (Player p : players) {
            StringBuilder handPart = new StringBuilder();

            // Get player hand -- pretty self explanatory
            List<Card> hand = p.getHand();
            for (int i = 0; i < hand.size(); i++) {
                //TODO: Encrypt value of cards
                //Card -- hand.get(i).toString()
                //Encrypted Card -- AESEncryption.encrypt(hand.get(i).toString(), key, AESEncryption.generateIV())
                handPart.append(hand.get(i).toString());


                //handPart.append(hand.get(i).toString()); // ex: 7 of Spades
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


        try {
            byte[] iv = AESEncryption.generateIV(); // Generate ONE IV for the whole payload
            String encryptedPayload = AESEncryption.encrypt(sb.toString(), BlackJackManager.key, iv);
            LOGGER.fine("Game state encrypted successfully.");
            // Prepend the IV to the ciphertext
            return combineIvAndCiphertext(iv, encryptedPayload);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to encrypt game state", e);
            throw new RuntimeException("Failed to encrypt game state", e);
        }

    }

    // loads a saved game string back into the game
    public static void loadState(BlackjackGame game, String encryptedState) {
        String decryptedPayload;
        //If empty
        if (encryptedState == null || encryptedState.isEmpty()) {
            LOGGER.warning("Load state called with empty or null state string.");
            return;
        }

        //Try Catch for splitting IV and ciphertext
        try {
            String[] parts = splitIvAndCiphertext(encryptedState);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid saved state format: missing IV delimiter.");
            }
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            String encryptedData = parts[1];

            // Decrypt payload using extracted IV
            decryptedPayload = AESEncryption.decrypt(encryptedData, BlackJackManager.key, iv);
            LOGGER.fine("Game state decrypted successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to decrypt game state", e);
            throw new RuntimeException("Failed to decrypt game state", e);
        }
        // splitting the save string into 2 parts: turnIndex and Player Data
        String[] parts = decryptedPayload.split("\\|", 2);


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
