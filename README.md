# Links  
contact _shih-ru.sheng@sjsu.edu_ if you have any problems, edit permission are only open to SJSU accounts  
Project Detail :  
Class Relation Diagram :  
Group BackLog : _https://docs.google.com/spreadsheets/d/1HoEkCLQPnJMT0byzuBFewir19dSWsc0M4cQS3lu45dM/edit?usp=sharing_  

# Overview
Our CS 151 Group Project #2. We built a Game Launcher/Manager as well as Blackjack and Snake in JavaFX. Our Game manager
login uses hashed and salted passwords for secure storage (using bcrypt). We've even added AES encryption for loading
and saving game state. Snake is snake, not much to talk about there.

# Design
![image](/UML.png)
_UML Diagram of our Codebase_
<br>
There's a lot of code, but its largely split into 3 categories:
<br>
Snake Game, BlackJack, and GameManager/Login.
<br>
<br>
AccountManager/HighScoreManager/MainMenuController: Aggregated details from games and displays them in the menu
<br>
Player/HumanPlayer: Class defines what a player does/operates
<br>
Deck: Class initializes all the cards in a full deck
<br>
Card: Class holds properties of a playing card + getters/setters
<br>
BlackJackManager: Handles saving and loading states as well as applying encryption/decryption
<br>
BlackJackGame: Handles Blackjack game logic and UI rendering
<br>
BlackjackController: Handles Blackjack menu options
<br>
AESEncryption: A wrapper around the javax crpyto library/package to implement AES
<br>
Open the image in a new tab and zoom in if you want to see more details.

# Installation Instructions
Pre-requisites:
- Java 9+ (Our pom.xml file is at Java 24, you can change based on whichever SDK version you have installed)
  -  Windows/Mac: https://www.oracle.com/java/technologies/downloads/
  -  Linux: Download the OpenJDK from your distro's package manager
- Maven 3.9.9
  - Earlier Versions may work, but we are currently using this one.
- Git Installed
  - (Optional, but better) SSH Key setup w/ Github
  (Tutorial here - https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
  - (The easy way) Have GitHub Desktop installed and sign in.

# Usage
Step 1: Clone Project from Github
<br>
Step 2: Change directory to the project folder (CS151_Sp_Sec6_Proj2_GamesInJavaFX)
<br>
Step 3: Run `mvn clean javafx:run`
<br>
Step 4: Profit!


# Contribution  
Ryan: This README, Encrypting/Decrypting Game state, Hashing passwords, and some other small contributions   

Sean:   Snake game, MainMenu, HighScore for Black Jack

Efe: I have added snake movement, highscore storing and displaying on snake game, and adding songs to both games.
<br>

Kanan: Implemented full Blackjack game logic with betting, balance handling, other features, and UI updates.



