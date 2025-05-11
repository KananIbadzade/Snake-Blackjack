package org.example.snakeblackjack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.lang.reflect.Field;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    private File originalFile;
    private File backupFile;

    @BeforeEach
    void setUp() throws Exception {
        // Create a reference to high_scores.txt
        originalFile = new File("high_scores.txt");

        // Create a backup if the file exists
        if (originalFile.exists()) {
            backupFile = new File(tempDir.toFile(), "high_scores_backup.txt");
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // Delete or create empty high_scores.txt for testing
        if (originalFile.exists()) {
            originalFile.delete();
        }
        originalFile.createNewFile();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Delete test file
        if (originalFile.exists()) {
            originalFile.delete();
        }

        // Restore backup if it exists
        if (backupFile != null && backupFile.exists()) {
            Files.copy(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    void testDefaultScoresForNewUser() {

        HighScoreManager scoreManager = new HighScoreManager();
        String username = "testUser";

        scoreManager.defaultScoresForUsers(username);


        assertEquals(1000, scoreManager.getSnakeScore(username),
                "New user should have default snake score of 1000");
        assertEquals(1000, scoreManager.getBlackjackScore(username),
                "New user should have default blackjack score of 1000");
    }

    @Test
    void testGetDefaultScoreForNonExistentUser() {
        HighScoreManager scoreManager = new HighScoreManager();

        assertEquals(1000, scoreManager.getSnakeScore("nonExistentUser"),
                "Non-existent users should get default snake score");
        assertEquals(1000, scoreManager.getBlackjackScore("nonExistentUser"),
                "Non-existent users should get default blackjack score");
    }

    @Test
    void testUpdateSnakeScoreHigherScore() {
        HighScoreManager scoreManager = new HighScoreManager();
        String username = "testUser";
        scoreManager.defaultScoresForUsers(username); // Initialize with default 1000

        scoreManager.updateSnakeScore(username, 1500);

        assertEquals(1500, scoreManager.getSnakeScore(username),
                "Snake score should be updated to higher value");
    }

    @Test
    void testUpdateSnakeScoreLowerScore() {
        HighScoreManager scoreManager = new HighScoreManager();
        String username = "testUser";
        scoreManager.defaultScoresForUsers(username); // Initialize with default 1000

        scoreManager.updateSnakeScore(username, 500);

        assertEquals(1000, scoreManager.getSnakeScore(username),
                "Snake score should not be updated to lower value");
    }

    @Test
    void testUpdateBlackjackScore() {
        HighScoreManager scoreManager = new HighScoreManager();
        String username = "testUser";
        scoreManager.defaultScoresForUsers(username); // Initialize with default 1000

        scoreManager.updateBlackjackScore(username, 1500);

        assertEquals(1500, scoreManager.getBlackjackScore(username),
                "Blackjack score should be updated unconditionally");

        scoreManager.updateBlackjackScore(username, 500);
        assertEquals(500, scoreManager.getBlackjackScore(username),
                "Blackjack score should be updated even to lower value");
    }

    @Test
    void testLoadScores() throws IOException {
        // Create a test file with known scores
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
            writer.write("user1:1500:2000");
            writer.newLine();
            writer.write("user2:800:1200");
            writer.newLine();
        }

        // Create a new manager that will load from this file
        HighScoreManager newManager = new HighScoreManager();

        // Assertions
        assertEquals(1500, newManager.getSnakeScore("user1"));
        assertEquals(2000, newManager.getBlackjackScore("user1"));
        assertEquals(800, newManager.getSnakeScore("user2"));
        assertEquals(1200, newManager.getBlackjackScore("user2"));
    }

    @Test
    void testSaveScores() throws IOException {
        // Manually setting scores
        HighScoreManager scoreManager = new HighScoreManager();
        scoreManager.defaultScoresForUsers("user1");
        scoreManager.updateSnakeScore("user1", 1500);
        scoreManager.updateBlackjackScore("user1", 2000);

        scoreManager.defaultScoresForUsers("user2");
        scoreManager.updateSnakeScore("user2", 1100);
        scoreManager.updateBlackjackScore("user2", 1200);

        // Save to File
        scoreManager.saveScores();

        // Assertions - read the file content and check it
        String content = Files.readString(originalFile.toPath());
        System.out.println(content);
        assertTrue(content.contains("user1:1500:2000"), "File is missing user1's scores");
        assertTrue(content.contains("user2:1100:1200"), "File is missing user2's scores");

        // Create a new manager and verify it loads the saved scores
        HighScoreManager newManager = new HighScoreManager();
        assertEquals(1500, newManager.getSnakeScore("user1"));
        assertEquals(2000, newManager.getBlackjackScore("user1"));
    }

    @Test
    void testLoadScoresWithInvalidFormat() throws IOException {
        // Arrange - create a test file with some invalid lines
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
            writer.write("user1:1500:2000"); // Valid
            writer.newLine();
            writer.write("invalid_line"); // Invalid
            writer.newLine();
            writer.write("user2:800"); // Invalid (missing third part)
            writer.newLine();
        }


        HighScoreManager newManager = new HighScoreManager();

        // Assert - only the valid line should be loaded
        assertEquals(1500, newManager.getSnakeScore("user1"));
        assertEquals(2000, newManager.getBlackjackScore("user1"));
        assertEquals(1000, newManager.getSnakeScore("user2"), "Invalid lines should be ignored");
    }

    @Test
    void testLoadScoresFileDoesNotExist() {
        // Delete any existing files to test DNE
        if (originalFile.exists()) {
            originalFile.delete();
        }


        HighScoreManager newManager = new HighScoreManager();

        // Assertion - should return 1000
        assertEquals(1000, newManager.getSnakeScore("anyUser"));
    }

    @Test
    void testGetMapsDirectly() throws Exception {
        // Reflection to access private maps to verify their contents

        // Set up User
        HighScoreManager scoreManager = new HighScoreManager();
        scoreManager.defaultScoresForUsers("user1");
        scoreManager.updateSnakeScore("user1", 1500);

        // Access the private maps
        Field snakeScoresField = HighScoreManager.class.getDeclaredField("snakeHighScores");
        snakeScoresField.setAccessible(true);
        Map<String, Integer> snakeScores = (Map<String, Integer>) snakeScoresField.get(scoreManager);

        Field blackjackScoresField = HighScoreManager.class.getDeclaredField("blackJackHighScores");
        blackjackScoresField.setAccessible(true);
        Map<String, Integer> blackjackScores = (Map<String, Integer>) blackjackScoresField.get(scoreManager);

        // Assertions
        assertEquals(1500, snakeScores.get("user1"));
        assertEquals(1000, blackjackScores.get("user1"));
        assertEquals(1, snakeScores.size(), "There should be one entry in snake scores map");
    }
}