package com.electionportal.models;

/**
 * Represents a candidate running in the election. (A POJO)
 * Demonstrates: Class, Object, Encapsulation
 */
public class Candidate {

    private String candidateId;
    private String name;
    private String party;
    private String platform;

    public Candidate(String candidateId, String name, String party, String platform) {
        this.candidateId = candidateId;
        this.name = name;
        this.party = party;
        this.platform = platform;
    }

    // --- Getters ---
    public String getCandidateId() { return candidateId; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public String getPlatform() { return platform; }

    public void display() {
        System.out.println("  ID: " + candidateId + " | Name: " + name + " (" + party + ")");
        System.out.println("     Platform: " + platform);
    }
}
