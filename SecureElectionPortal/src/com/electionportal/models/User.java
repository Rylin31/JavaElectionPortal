package com.electionportal.models;

import com.electionportal.services.ElectionManager;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

/**
 * ABSTRACT base class representing any user.
 * Demonstrates: Abstraction, Encapsulation
 */
public abstract class User {

    // Encapsulation: all fields are private, accessed via getters
    private String userId;
    private String username;
    private String fullName;
    private String hashedPassword;

    public User(String userId, String username, String fullName, String hashedPassword) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.hashedPassword = hashedPassword;
    }

    // --- Getters for Encapsulation ---
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    // Used by DataManager to save the hash
    public String getHashedPasswordForSave() { return hashedPassword; }

    // --- Abstract Methods (Polymorphism) ---
    
    /**
     * @return A string representing the user type (e.g., "VOTER")
     */
    public abstract String getUserType();

    /**
     * POLYMORPHIC method. Each user type (Voter, Admin)
     * will have a different dashboard.
     * @param manager The main ElectionManager
     * @param scanner A scanner for user input
     */
    public abstract void showDashboard(ElectionManager manager, Scanner scanner);


    // --- Security Logic ---

    /**
     * Simulates validating a password against a stored SHA-256 hash.
     */
    public boolean validatePassword(String password) {
        return this.hashedPassword.equals(simulateHash(password));
    }

    /**
     * Helper to "hash" a new password using SHA-256.
     * This is far more "impressive" and secure than using hashCode().
     */
    public static String simulateHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            // In a real app, this should be handled more gracefully
            // For the demo, we fall back to a simple (bad) hash.
            System.err.println("SHA-256 not available, using insecure hash!");
            return Integer.toString(password.hashCode());
        }
    }
}
