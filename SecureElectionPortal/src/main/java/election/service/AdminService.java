package election.service;

// import election.model.Admin;
import election.model.Voter;
import election.model.Candidate; //  ADD THIS LINE!
import election.repository.*;

public class AdminService {
    private final UserRepository userRepo;
    private final CandidateRepository candidateRepo;
    private final AuditLogRepository auditRepo;

    public AdminService(UserRepository userRepo, CandidateRepository candidateRepo, AuditLogRepository auditRepo) {
        this.userRepo = userRepo;
        this.candidateRepo = candidateRepo;
        this.auditRepo = auditRepo;
    }

    public void registerVoter(String id, String name, String password) {
        if (userRepo.userExists(id))
            throw new IllegalArgumentException("User ID already exists");
        Voter voter = new Voter(id, name, password);
        userRepo.addUser(voter);
        auditRepo.logAction("Voter registered: " + name);
    }

    public void addCandidate(String id, String name, String party) {
        Candidate candidate = new Candidate(id, name, party);
        candidateRepo.addCandidate(candidate);
        auditRepo.logAction("Candidate added: " + name);
    }
}

