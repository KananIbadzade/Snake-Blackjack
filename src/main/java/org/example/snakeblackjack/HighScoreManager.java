package org.example.snakeblackjack;

import java.io.*;
import java.util.*;


public class HighScoreManager {

    private static final String FILE_NAME = "high_scores.txt";
    private static final int DEFAULT_SCORE = 1000;
    private Map<String, Integer> snakeHighScores = new HashMap<>();
    private Map<String, Integer> blackJackHighScores = new HashMap<>();

    public HighScoreManager() {

        loadScores();
    }

    private void loadScores() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        //reads file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] splittedLines = line.split(":");
                if(splittedLines.length == 3){
                    String username = splittedLines[0];
                    int snake = Integer.parseInt(splittedLines[1]);
                    int blackJack = Integer.parseInt(splittedLines[2]);
                    snakeHighScores.put(username, snake);
                    blackJackHighScores.put(username, blackJack);
                }
            }
        }
        catch(IOException e){
            System.out.println("Error could not read highscore file");
        }
    }

    public void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String username : snakeHighScores.keySet()) {
                int snake = snakeHighScores.get(username);
                int blackjack = blackJackHighScores.getOrDefault(username, DEFAULT_SCORE); // just in case
                writer.write(username + ":" + snake + ":" + blackjack);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error could not write high scores file.");
        }
    }

    public void defaultScoresForUsers(String username){
        if(!snakeHighScores.containsKey(username)){
            snakeHighScores.put(username, DEFAULT_SCORE);
        }
        if(!blackJackHighScores.containsKey(username)){
            blackJackHighScores.put(username, DEFAULT_SCORE);
        }
        saveScores();
    }

    public int getSnakeScore(String username) {
        return snakeHighScores.getOrDefault(username, DEFAULT_SCORE);
    }

    public int getBlackjackScore(String username) {
        return blackJackHighScores.getOrDefault(username, DEFAULT_SCORE);
    }


    public void updateSnakeScore(String username, int newScore) {
        int currentScore = snakeHighScores.getOrDefault(username, DEFAULT_SCORE);
//        if (newScore > currentScore) {
//              snakeHighScores.put(username, newScore);
//            saveScores();
//        }
        if (currentScore == DEFAULT_SCORE || newScore > currentScore) {
            snakeHighScores.put(username, newScore);
            saveScores();
        }
    }


    public void updateBlackjackScore(String username, int newScore) {
        blackJackHighScores.put(username, newScore);
        saveScores();
    }
    public List<Map.Entry<String, Integer>> getTop5SnakeScores() {
        return snakeHighScores.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue()) // sort descending
                .limit(5)
                .toList();
    }

    public List<Map.Entry<String, Integer>> getTop5BlackScores() {
        return blackJackHighScores.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue()) // sort descending
            .limit(5)
            .toList();
    }
}
