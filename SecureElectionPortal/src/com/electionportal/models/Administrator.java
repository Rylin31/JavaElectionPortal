package com.electionportal.models;

import com.electionportal.Main;
import com.electionportal.services.ElectionManager;
import java.util.Scanner;

/**
 * Represents an administrator.
 * Demonstrates: INHERITANCE
 */
public class Administrator extends User {

    public Administrator(String userId, String username, String fullName, String hashedPassword) {
        // Call the base class constructor
        super(userId, username, fullName, hashedPassword);
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }

    /**
     * POLYMORPHIC implementation for an Admin's dashboard.
     */
    @Override
    public void showDashboard(ElectionManager manager, Scanner scanner) {
        System.out.println("\n===== Admin Dashboard: " + getFullName() + " =====");
        System.out.println("Election Status: " + manager.getCurrentState() + "\n");

        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("1. Manage Election State");
            System.out.println("2. Register New Voter");
            System.out.println("3. Add New Candidate");
            System.out.println("4. View All Registered Users");
            System.out.println("5. View All Candidates");
            System.out.println("6. Run Vote Tally");
            System.out.println("7. Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    Main.adminManageState(manager, scanner);
                    break;
                case "2":
                    Main.adminRegisterVoter(manager, scanner);
                    break;
                case "3":
                    Main.adminAddCandidate(manager, scanner);
                    break;
                case "4":
                    manager.displayUsers();
                    break;
                case "5":
                    manager.displayCandidates();
                    break;
                case "6":
                    Main.adminRunTally(manager);
                    break;
                case "7":
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }
}
