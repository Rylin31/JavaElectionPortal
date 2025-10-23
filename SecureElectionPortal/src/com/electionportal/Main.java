package com.electionportal;

import com.electionportal.models.*;
import com.electionportal.services.*;
import java.io.Console;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application entry point.
 * Handles all Console UI logic.
 */
public class Main {

    // These are the main "controllers" of the application
    private static ElectionManager electionManager;
    private static AuthService authService;
    private static User currentUser; // Holds the logged-in user
    private static Scanner scanner; // Global scanner to avoid resource issues

    public static void main(String[] args) {
        
        System.out.println("Welcome to the Secure Election Portal (Java Edition)");
        System.out.println("---------------------------------------------------");

        // 1. Initialize the core services
        electionManager = new ElectionManager();
        authService = new AuthService(electionManager.getUsers());
        scanner = new Scanner(System.in);

        // 2. Main application loop
        try {
            while (true) {
                if (currentUser == null) {
                    showLogin();
                } else {
                    // POLYMORPHISM in action!
                    // This one line calls the correct dashboard
                    // based on whether currentUser is a Voter or an Administrator.
                    currentUser.showDashboard(electionManager, scanner);

                    // After showDashboard() finishes, the user has logged out
                    currentUser = null;
                    System.out.println("\nYou have been logged out.");
                    electionManager.saveAllData(); // Ensure data is saved on logout
                }
            }
        } finally {
            scanner.close(); // Close the scanner on exit
        }
    }

    /**
     * Handles the login UI flow.
     */
    private static void showLogin() {
        System.out.println("\nPlease log in to continue.");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        // Use the secure password reader
        String password = readPassword();

        currentUser = authService.login(username, password);

        if (currentUser == null) {
            System.out.println("Login failed. Invalid username or password.\n");
        } else {
            System.out.println("\nLogin successful. Welcome, " + currentUser.getFullName() + "!");
        }
    }

    /**
     * Helper to mask password input in the console.
     * Falls back to plain text if System.console() is not available (e.g., in some IDEs).
     */
    private static String readPassword() {
        Console console = System.console();
        if (console == null) {
            System.out.print("Password (IDE fallback, will be visible): ");
            return scanner.nextLine();
        }
        // This is the secure, standard Java way
        return new String(console.readPassword("Password: "));
    }


    // --- UI Methods (Called by Dashboard) ---
    // These are static and public so the Model classes can call them.
    // We must pass the scanner to them.

    // --- Voter Methods ---
    public static void voterCastVote(ElectionManager manager, Voter voter, Scanner scanner) {
        if (manager.getCurrentState() != ElectionState.VOTING) {
            System.out.println("\nVoting is not currently open.\n");
            return;
        }
        if (voter.hasVoted()) {
            System.out.println("\nYou have already cast your vote.\n");
            return;
        }

        manager.displayCandidates();
        System.out.print("Enter the ID of the candidate you wish to vote for: ");
        String id = scanner.nextLine();

        if (manager.castVote(voter, id)) {
            System.out.println("\n*** Your vote has been cast successfully! ***\n");
        } else {
            System.out.println("\nVote casting failed. Please check the ID and try again.\n");
        }
    }

    // --- Admin Methods ---

    public static void adminManageState(ElectionManager manager, Scanner scanner) {
        System.out.println("\nCurrent State: " + manager.getCurrentState());
        System.out.println("Select new state:");
        System.out.println("1. REGISTRATION");
        System.out.println("2. VOTING");
        System.out.println("3. CLOSED");
        System.out.print("Selection: ");

        ElectionState newState = manager.getCurrentState();
        
        switch (scanner.nextLine()) {
            case "1": newState = ElectionState.REGISTRATION; break;
            case "2": newState = ElectionState.VOTING; break;
            case "3": newState = ElectionState.CLOSED; break;
            default: System.out.println("Invalid selection."); return;
        }

        if (manager.changeState(newState)) {
            System.out.println("\n*** Election state changed to " + newState + " ***\n");
        } else {
            System.out.println("\nState was not changed.\n");
        }
    }

    public static void adminRegisterVoter(ElectionManager manager, Scanner scanner) {
        if (manager.getCurrentState() != ElectionState.REGISTRATION) {
            System.out.println("\nCan only register voters during 'REGISTRATION' phase.\n");
            return;
        }
        System.out.println("\n--- Register New Voter ---");
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine(); // Admin is setting password, fine to be visible

        if (manager.registerVoter(user, name, pass)) {
            System.out.println("\n*** Voter '" + name + "' registered successfully. ***\n");
        } else {
            System.out.println("\nRegistration failed. Username may be taken.\n");
        }
    }

    public static void adminAddCandidate(ElectionManager manager, Scanner scanner) {
        if (manager.getCurrentState() != ElectionState.REGISTRATION) {
            System.out.println("\nCan only add candidates during 'REGISTRATION' phase.\n");
            return;
        }
        System.out.println("\n--- Add New Candidate ---");
        System.out.print("Candidate Name: ");
        String name = scanner.nextLine();
        System.out.print("Party: ");
        String party = scanner.nextLine();
        System.out.print("Platform: ");
        String platform = scanner.nextLine();

        if (manager.addCandidate(name, party, platform)) {
            System.out.println("\n*** Candidate '" + name + "' added successfully. ***\n");
        } else {
            System.out.println("\nFailed to add candidate. Name may be taken.\n");
        }
    }

    public static void adminRunTally(ElectionManager manager) {
        System.out.println("\n--- Running Vote Tally ---");
        Map<String, Integer> results = manager.tallyVotes();

        if (results == null) {
            System.out.println("Tally could not be run. Check election state.\n");
            return;
        }

        System.out.println("===== ELECTION RESULTS =====");
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " vote(s)");
        }
        System.out.println("============================\n");
    }
}
