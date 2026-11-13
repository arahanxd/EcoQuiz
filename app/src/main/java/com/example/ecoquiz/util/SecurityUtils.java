package com.example.ecoquiz.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Security helper utility for hashing passwords with cryptographic salt.
 * Ensures that passwords are never stored in plain text in the SQLite database.
 */
public class SecurityUtils {

    /**
     * Generates a random cryptographic salt encoded in hexadecimal.
     * @return 16-byte random salt as hex string.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }

    /**
     * Hashes the plaintext password combined with the salt using SHA-256.
     * @param password Plaintext password provided by user.
     * @param salt Hex-encoded salt string.
     * @return Hex-encoded SHA-256 hash string.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = salt + password;
            byte[] hashBytes = digest.digest(input.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifies if a user-entered password matches the stored salt and hash.
     * @param enteredPassword Plaintext password entered at login.
     * @param storedSalt Salt stored in SQLite.
     * @param storedHash SHA-256 hash stored in SQLite.
     * @return true if password matches, false otherwise.
     */
    public static boolean verifyPassword(String enteredPassword, String storedSalt, String storedHash) {
        if (enteredPassword == null || storedSalt == null || storedHash == null) {
            return false;
        }
        String computedHash = hashPassword(enteredPassword, storedSalt);
        return storedHash.equals(computedHash);
    }

    /**
     * Converts a byte array to a lowercase hex string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
