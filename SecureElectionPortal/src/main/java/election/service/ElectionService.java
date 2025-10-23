package election.service;

import election.model.Candidate; //  ADD THIS LINE!
import election.model.Vote; //  ADD THIS LINE!
import election.repository.*;
import java.util.Comparator;
import java.util.List;

public class ElectionService {
    private boolean electionStarted = false;
    private boolean electionEnded = false;
    private final VoteRepository voteRepo;
    private final CandidateRepository candidateRepo;

    public ElectionService(VoteRepository voteRepo, CandidateRepository candidateRepo) {
        this.voteRepo = voteRepo;
        this.candidateRepo = candidateRepo;
    }

    public void startElection() {
        if (candidateRepo.getAllCandidates().isEmpty()) {
            throw new IllegalStateException("Add candidates first");
        }
        electionStarted = true;
        voteRepo.clearVotes();
        candidateRepo.resetVotes();
    }

    public void endElection() {
        electionStarted = false;
        electionEnded = true;
        tallyVotes();
    }

    private void tallyVotes() {
        List<Candidate> candidates = candidateRepo.getAllCandidates();
        for (Candidate c : candidates) {
            c.resetVotes();
        }
        voteRepo.getAllVotes().stream()
                .filter(Vote::verifyIntegrity)
                .forEach(vote -> {
                    Candidate c = candidateRepo.findById(vote.getCandidateId());
                    if (c != null)
                        c.incrementVotes();
                });
    }

    public Candidate getWinner() {
        return candidateRepo.getAllCandidates().stream()
                .max(Comparator.comparingInt(Candidate::getVotes))
                .orElse(null);
    }

    public boolean isElectionStarted() {
        return electionStarted;
    }

    public boolean isElectionEnded() {
        return electionEnded;
    }
}

