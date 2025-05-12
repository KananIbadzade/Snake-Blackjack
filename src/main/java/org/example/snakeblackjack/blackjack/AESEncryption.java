package org.example.snakeblackjack.blackjack;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.SecureRandom;

/**
 * AES Encryption and Decryption.
 * Was originally gonna use ROT13 but why not use the latest and greatest encryption
 */
public class AESEncryption {

    /**
     *
     * @return
     * @throws Exception
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Options: 128/192/256 bits
        return keyGenerator.generateKey();
    }

    /**
     * Generates a random IV for use with AES encryption.
     *
     * @return
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     *
     * @param plainText
     * @param key
     * @param iv
     * @return Encrypted String in Base64 encoding.
     * @throws Exception
     */
    public static String encrypt(String plainText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     *
     * @param cipherText
     * @param key
     * @param iv
     * @return Decrypted String in UTF-8 encoding.
     * @throws Exception
     */
    public static String decrypt(String cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, "UTF-8");
    }
}
