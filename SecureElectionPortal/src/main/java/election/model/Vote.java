package election.model;

import election.security.VoteIntegrity;
import java.util.Date;

public class Vote {
    private final String voterId;
    private final String candidateId;
    private final String timestamp;
    private final String hash;

    public Vote(String voterId, String candidateId) {
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.timestamp = new Date().toString();
        this.hash = VoteIntegrity.generateVoteHash(voterId, candidateId, timestamp);
    }

    public boolean verifyIntegrity() {
        return VoteIntegrity.verifyVoteIntegrity(voterId, candidateId, timestamp, hash);
    }

    // Getters
    public String getVoterId() {
        return voterId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return String.format("Voter: %s   Candidate: %s | %s | Hash: %s",
                voterId, candidateId, timestamp, hash);
    }
}

