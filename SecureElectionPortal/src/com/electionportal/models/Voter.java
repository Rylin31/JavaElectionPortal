package com.electionportal.models;

import com.electionportal.Main;
import com.electionportal.services.ElectionManager;
import java.util.Scanner;

/**
 * Represents a voter.
 * Demonstrates: INHERITANCE
 */
public class Voter extends User {

    private boolean hasVoted;

    public Voter(String userId, String username, String fullName, String hashedPassword, boolean hasVoted) {
        // Call the base class constructor
        super(userId, username, fullName, hashedPassword);
        this.hasVoted = hasVoted;
    }

    // --- Getters and Setters ---
    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    @Override
    public String getUserType() {
        return "VOTER";
    }

    /**
     * POLYMORPHIC implementation for a Voter's dashboard.
     */
    @Override
    public void showDashboard(ElectionManager manager, Scanner scanner) {
        System.out.println("\n===== Voter Dashboard: " + getFullName() + " =====");
        System.out.println("Election Status: " + manager.getCurrentState() + "\n");

        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("1. View Candidates");
            System.out.println("2. Cast Vote");
            System.out.println("3. View My Status");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    manager.displayCandidates();
                    break;
                case "2":
                    // Calls static UI method in Main.java
                    Main.voterCastVote(manager, this, scanner);
                    break;
                case "3":
                    System.out.println("\nStatus for " + getFullName() + ": " + (hasVoted ? "You have already voted." : "You have NOT voted yet.") + "\n");
                    break;
                case "4":
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }
}
