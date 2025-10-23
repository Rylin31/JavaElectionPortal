package election.service;

import election.model.Voter;
import election.model.Candidate;
import election.model.Vote;
import election.repository.*;

public class VoterService {
    private final VoteRepository voteRepo;
    private final CandidateRepository candidateRepo;
    private final AuditLogRepository auditRepo;

    public VoterService(VoteRepository voteRepo, CandidateRepository candidateRepo,
            AuditLogRepository auditRepo) {
        this.voteRepo = voteRepo;
        this.candidateRepo = candidateRepo;
        this.auditRepo = auditRepo;
    }

    public void castVote(Voter voter, String candidateId) {
        Candidate candidate = candidateRepo.findById(candidateId);
        if (candidate == null) {
            throw new IllegalArgumentException("Invalid candidate ID");
        }

        Vote vote = new Vote(voter.getId(), candidateId);
        if (!vote.verifyIntegrity()) {
            throw new SecurityException("Vote integrity check failed");
        }

        voteRepo.addVote(vote);
        candidate.incrementVotes();
        candidateRepo.addCandidate(candidate); // Save updated votes
        auditRepo.logAction("Vote cast by " + voter.getName() + " for " + candidate.getName());
        voter.setHasVoted(true);
    }
}
