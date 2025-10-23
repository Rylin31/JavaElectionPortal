package com.electionportal.models;

/**
 * Represents the different phases of the election.
 */
public enum ElectionState {
    REGISTRATION, // Admin can add voters and candidates
    VOTING,       // Voters can cast votes
    CLOSED        // Tally can be run, no more voting
}
