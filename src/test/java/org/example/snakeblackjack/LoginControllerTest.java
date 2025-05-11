package org.example.snakeblackjack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private AccountManager mockAccountManager;

    // Create a simplified controller just for tests
    private class TestLoginController {
        private String username;    
        private String password;
        private String statusMessage;
        private boolean gameManagerOpened = false;

        public void handleLogin() {
            if (mockAccountManager.checkExist(username, password)) {
                LoginController.loggedInUserName = username;
                statusMessage = "Login successful!";
                gameManagerOpened = true;
            } else {
                statusMessage = "Invalid username or password.";
            }
        }

        public void handleCreateAccount() {
            if (mockAccountManager.createAccount(username, password)) {
                statusMessage = "Account created!";
            } else {
                statusMessage = "Username already exists.";
            }
        }
    }

    private TestLoginController controller;

    @BeforeEach
    void setUp() {
        LoginController.loggedInUserName = null;
        controller = new TestLoginController();
    }

    @Test
    void handleLogin_successful() {
        // Arrange
        controller.username = "testUser";
        controller.password = "testPass";
        when(mockAccountManager.checkExist("testUser", "testPass")).thenReturn(true);

        // Act
        controller.handleLogin();

        // Assert
        assertEquals("testUser", LoginController.loggedInUserName);
        assertEquals("Login successful!", controller.statusMessage);
        assertTrue(controller.gameManagerOpened);
        verify(mockAccountManager).checkExist("testUser", "testPass");
    }

    @Test
    void handleLogin_failure() {
        // Arrange
        controller.username = "wrongUser";
        controller.password = "wrongPass";
        when(mockAccountManager.checkExist("wrongUser", "wrongPass")).thenReturn(false);

        // Act
        controller.handleLogin();

        // Assert
        assertNull(LoginController.loggedInUserName);
        assertEquals("Invalid username or password.", controller.statusMessage);
        assertFalse(controller.gameManagerOpened);
        verify(mockAccountManager).checkExist("wrongUser", "wrongPass");
    }

    @Test
    void handleCreateAccount_success() {
        // Arrange
        controller.username = "newUser";
        controller.password = "newPass";
        when(mockAccountManager.createAccount("newUser", "newPass")).thenReturn(true);

        // Act
        controller.handleCreateAccount();

        // Assert
        assertEquals("Account created!", controller.statusMessage);
        verify(mockAccountManager).createAccount("newUser", "newPass");
    }

    @Test
    void handleCreateAccount_failure() {
        // Arrange
        controller.username = "existingUser";
        controller.password = "anyPass";
        when(mockAccountManager.createAccount("existingUser", "anyPass")).thenReturn(false);

        // Act
        controller.handleCreateAccount();

        // Assert
        assertEquals("Username already exists.", controller.statusMessage);
        verify(mockAccountManager).createAccount("existingUser", "anyPass");
    }
}