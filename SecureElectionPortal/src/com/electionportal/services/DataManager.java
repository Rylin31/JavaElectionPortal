package com.electionportal.services;

import com.electionportal.models.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all File I/O operations.
 * Demonstrates: File I/O (java.nio and java.io), Separation of Concerns
 */
public class DataManager {

    private static final String DATA_DIR = "data";
    private static final Path USERS_FILE = Paths.get(DATA_DIR, "users.csv");
    private static final Path CANDIDATES_FILE = Paths.get(DATA_DIR, "candidates.csv");
    private static final Path VOTES_FILE = Paths.get(DATA_DIR, "votes.csv");
    private static final Path STATE_FILE = Paths.get(DATA_DIR, "state.txt");

    public DataManager() {
        try {
            // Ensure the data directory exists
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    // --- User Management ---
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        if (!Files.exists(USERS_FILE)) return users;

        // Use try-with-resources for automatic file closing
        try (BufferedReader reader = Files.newBufferedReader(USERS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String userType = parts[0];
                String id = parts[1];
                String username = parts[2];
                String fullName = parts[3];
                String hash = parts[4];

                if ("VOTER".equals(userType) && parts.length == 6) {
                    boolean hasVoted = Boolean.parseBoolean(parts[5]);
                    users.add(new Voter(id, username, fullName, hash, hasVoted));
                } else if ("ADMIN".equals(userType)) {
                    users.add(new Administrator(id, username, fullName, hash));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        // Use try-with-resources with a PrintWriter for easy line writing
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE.toFile()))) {
            for (User user : users) {
                String line = String.join(",",
                        user.getUserType(),
                        user.getUserId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getHashedPasswordForSave()
                );
                
                // Use Java 16+ Pattern Matching for instanceof
                if (user instanceof Voter v) {
                    line += "," + v.hasVoted();
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // --- Candidate Management ---
    public List<Candidate> loadCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        if (!Files.exists(CANDIDATES_FILE)) return candidates;

        try (BufferedReader reader = Files.newBufferedReader(CANDIDATES_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Use "|" separator for platforms that might have commas
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    candidates.add(new Candidate(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading candidates: " + e.getMessage());
        }
        return candidates;
    }

    public void saveCandidates(List<Candidate> candidates) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATES_FILE.toFile()))) {
            for (Candidate c : candidates) {
                // Use "|" separator
                String line = String.join("|", c.getCandidateId(), c.getName(), c.getParty(), c.getPlatform());
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving candidates: " + e.getMessage());
        }
    }

    // --- Vote Management ---
    public List<Vote> loadVotes() {
        List<Vote> votes = new ArrayList<>();
        if (!Files.exists(VOTES_FILE)) return votes;

        try (BufferedReader reader = Files.newBufferedReader(VOTES_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    votes.add(new Vote(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading votes: " + e.getMessage());
        }
        return votes;
    }

    public void saveVotes(List<Vote> votes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTES_FILE.toFile()))) {
            for (Vote v : votes) {
                writer.println(v.getVoterId() + "," + v.getCandidateId());
            }
        } catch (IOException e) {
            System.err.println("Error saving votes: " + e.getMessage());
        }
    }

    // --- State Management ---
    public ElectionState loadState() {
        if (!Files.exists(STATE_FILE)) return ElectionState.REGISTRATION;
        try {
            String stateStr = Files.readString(STATE_FILE).trim();
            return ElectionState.valueOf(stateStr);
        } catch (Exception e) {
            return ElectionState.REGISTRATION;
        }
    }

    public void saveState(ElectionState state) {
        try {
            Files.writeString(STATE_FILE, state.toString());
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }
}
