package com.electionportal.services;

import com.electionportal.models.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The core logic engine of the application.
 * Manages all collections and business rules.
 * Demonstrates: Collections (List, Map), Encapsulation, Java Streams
 */
public class ElectionManager {

    private DataManager dataManager;

    // COLLECTIONS: These lists hold the application's state.
    private List<User> users;
    private List<Candidate> candidates;
    private List<Vote> votes;

    private ElectionState currentState;

    public ElectionManager() {
        this.dataManager = new DataManager();
        loadAllData();
    }
    
    // --- Public Getters ---
    public ElectionState getCurrentState() { return currentState; }
    public List<User> getUsers() { return users; }

    
    // --- Data Load/Save ---
    public void loadAllData() {
        users = dataManager.loadUsers();
        candidates = dataManager.loadCandidates();
        votes = dataManager.loadVotes();
        currentState = dataManager.loadState();

        // Create a default admin if no users exist
        if (users.isEmpty()) {
            System.out.println("No users found. Creating default admin...");
            System.out.println("Username: admin, Password: password");
            String adminHash = User.simulateHash("password");
            users.add(new Administrator("a-001", "admin", "Default Admin", adminHash));
            saveAllData();
        }
    }

    public void saveAllData() {
        dataManager.saveUsers(users);
        dataManager.saveCandidates(candidates);
        dataManager.saveVotes(votes);
        dataManager.saveState(currentState);
    }

    // --- State Logic ---
    public boolean changeState(ElectionState newState) {
        if (newState == currentState) return false;
        currentState = newState;
        saveAllData();
        return true;
    }

    // --- Voter Logic ---
    public boolean registerVoter(String username, String fullName, String password) {
        if (currentState != ElectionState.REGISTRATION) {
            System.out.println("Error: Can only register voters during 'REGISTRATION' phase.");
            return false;
        }
        
        // Use Java Streams to check if user exists
        boolean userExists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));

        if (userExists) {
            System.out.println("Error: Username already exists.");
            return false;
        }

        // Use Java Streams to get count for new ID
        long voterCount = users.stream().filter(u -> u instanceof Voter).count();
        String newId = String.format("v-%03d", voterCount + 1);
        String hash = User.simulateHash(password);
        users.add(new Voter(newId, username, fullName, hash, false));
        saveAllData();
        return true;
    }

    // --- Candidate Logic ---
    public boolean addCandidate(String name, String party, String platform) {
        if (currentState != ElectionState.REGISTRATION) {
            System.out.println("Error: Can only add candidates during 'REGISTRATION' phase.");
            return false;
        }
        
        boolean candidateExists = candidates.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));

        if (candidateExists) {
            System.out.println("Error: Candidate name already exists.");
            return false;
        }

        String newId = String.format("c-%03d", candidates.size() + 1);
        candidates.add(new Candidate(newId, name, party, platform));
        saveAllData();
        return true;
    }

    // --- Voting Logic ---
    public boolean castVote(Voter voter, String candidateId) {
        if (currentState != ElectionState.VOTING) {
            System.out.println("Error: Voting is not currently open.");
            return false;
        }
        if (voter.hasVoted()) {
            System.out.println("Error: You have already cast your vote.");
            return false;
        }
        
        Candidate candidate = candidates.stream()
                .filter(c -> c.getCandidateId().equals(candidateId))
                .findFirst()
                .orElse(null);

        if (candidate == null) {
            System.out.println("Error: Invalid Candidate ID.");
            return false;
        }

        // Cast the vote
        votes.add(new Vote(voter.getUserId(), candidate.getCandidateId()));
        voter.setHasVoted(true); // Mark voter as having voted
        saveAllData(); // Save votes and updated user status
        return true;
    }

    // --- Tally Logic ---
    public Map<String, Integer> tallyVotes() {
        if (currentState != ElectionState.CLOSED) {
            System.out.println("Error: Cannot tally votes until the election is 'CLOSED'.");
            return null;
        }

        // This is an "impressive" and modern Java 8+ way to tally votes.
        
        // 1. Group all votes by candidateId and count them.
        Map<String, Long> voteCounts = votes.stream()
                .collect(Collectors.groupingBy(
                        Vote::getCandidateId, 
                        Collectors.counting()
                ));

        // 2. Map candidate IDs to candidate Names, including those with 0 votes.
        return candidates.stream()
                .collect(Collectors.toMap(
                        Candidate::getName, // Key is the candidate's name
                        c -> voteCounts.getOrDefault(c.getCandidateId(), 0L).intValue() // Value is their vote count
                ));
    }

    // --- Display Helpers ---
    public void displayCandidates() {
        System.out.println("\n--- Official Candidates ---");
        if (candidates.isEmpty()) {
            System.out.println("No candidates have been registered.");
        }
        for (Candidate c : candidates) {
            c.display();
        }
        System.out.println("---------------------------\n");
    }

    public void displayUsers() {
        System.out.println("\n--- Registered Users ---");
        for (User user : users) {
            System.out.println("  ID: " + user.getUserId() + " | Type: " + user.getUserType() + " | Name: " + user.getFullName() + " | Username: " + user.getUsername());
            if (user instanceof Voter v) {
                System.out.println("     Has Voted: " + v.hasVoted());
            }
        }
        System.out.println("------------------------\n");
    }
}
