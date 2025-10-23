package com.electionportal.models;

/**
 * Represents a single vote, linking a voter to a candidate. (A POJO)
 * This object ensures anonymity; it doesn't store the voter's name.
 */
public class Vote {

    private String voterId;
    private String candidateId;

    public Vote(String voterId, String candidateId) {
        this.voterId = voterId;
        this.candidateId = candidateId;
    }

    // --- Getters ---
    public String getVoterId() { return voterId; }
    public String getCandidateId() { return candidateId; }
}
