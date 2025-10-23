package election.repository;

import election.model.Candidate;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateRepository {
    private final List<Candidate> candidates = new ArrayList<>();
    private final String filePath;

    public CandidateRepository(String filePath) {
        this.filePath = filePath;
        loadCandidates();
    }

    @SuppressWarnings("unchecked")
    private void loadCandidates() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            candidates.addAll((List<Candidate>) ois.readObject());
        } catch (Exception e) {
            System.out.println("Initializing empty candidate repository");
        }
    }

    private void saveCandidates() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(candidates);
        } catch (IOException e) {
            System.err.println("Error saving candidates: " + e.getMessage());
        }
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
        saveCandidates();
    }

    public List<Candidate> getAllCandidates() {
        return new ArrayList<>(candidates);
    }

    public Candidate findById(String id) {
        return candidates.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void resetVotes() {
        candidates.forEach(Candidate::resetVotes);
        saveCandidates();
    }
}
