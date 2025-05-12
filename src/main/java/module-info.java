module org.example.snakeblackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires bcrypt;

    opens org.example.snakeblackjack to javafx.fxml;
    opens org.example.snakeblackjack.blackjack to javafx.fxml;
    exports org.example.snakeblackjack;
    exports org.example.snakeblackjack.blackjack;
}
