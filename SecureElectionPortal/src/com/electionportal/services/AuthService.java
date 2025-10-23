package com.electionportal.services;

import com.electionportal.models.User;
import java.util.List;

/**
 * Handles user authentication.
 * Demonstrates: Encapsulation, Single Responsibility Principle
 */
public class AuthService {

    // This service OPERATES ON the list from ElectionManager
    private final List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    /**
     * Attempts to log a user in.
     *
     * @return The User object if successful, or null if not.
     */
    public User login(String username, String password) {
        
        // Find the user by username using modern Java Streams
        User user = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return null; // User not found
        }

        // Let the User object validate its own password (Encapsulation)
        if (user.validatePassword(password)) {
            return user; // Success
        }

        return null; // Invalid password
    }
}
