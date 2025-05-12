package org.example.snakeblackjack;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHash {

private String hashed;

//Empty Default Constructor
public PasswordHash() {

}

// Constructor to create a new instance of the PasswordHash class and initializes with hashed password.
public PasswordHash(String password) {
    this.hashed = BCrypt.withDefaults().hashToString(10, password.toCharArray());
}

public void setHashedPassword(String password) {
    this.hashed = BCrypt.withDefaults().hashToString(10, password.toCharArray());
}

// Returns the hashed password.
// This method is used to store the hashed password in the database.
public String getHashedPassword() {
   return this.hashed;
}

// Returns true if the password matches the hashed password, false otherwise.
// @param password - Plaintext password to check
// @param hashedPassword - Hashed password to check against
public Boolean checkStoredPassword(String password, String hashedPassword) {
    return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
}




//Debugging Function
public void printPassword() {
    System.out.println("Hashed Password:" + hashed);
}

}
