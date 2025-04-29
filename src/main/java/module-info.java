module org.example.snakeblackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.xml.dom;

    opens org.example.snakeblackjack to javafx.fxml;  // Ensure FXML can access this package at runtime
    exports org.example.snakeblackjack;  // Export the package to make it available for other modules
}
