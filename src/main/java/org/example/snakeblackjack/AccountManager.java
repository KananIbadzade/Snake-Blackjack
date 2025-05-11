package org.example.snakeblackjack;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String FILE_NAME = "user_accounts.txt";
    private Map<String, String> accounts = new HashMap<>();

    public AccountManager() {
        loadAccounts();
    }

    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    accounts.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // If file doesn't exist, ignore
        }
    }

    public boolean checkExist(String username, String password) {
        return accounts.containsKey(username) && accounts.get(username).equals(password);
    }

    public boolean createAccount(String username, String password) {
        if (accounts.containsKey(username)) return false;
        accounts.put(username, password);
        saveAccounts();
        return true;
    }

    private void saveAccounts() {
        try (BufferedWriter f_write = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> entry : accounts.entrySet()) {
                f_write.write(entry.getKey() + ":" + entry.getValue());
                f_write.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

