package election.repository;

import election.model.Vote;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VoteRepository {
    private final List<Vote> votes = new ArrayList<>();
    private final String filePath;

    public VoteRepository(String filePath) {
        this.filePath = filePath;
        loadVotes();
    }

    @SuppressWarnings("unchecked")
    private void loadVotes() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            votes.addAll((List<Vote>) ois.readObject());
        } catch (Exception e) {
            System.out.println("Initializing empty vote repository");
        }
    }

    private void saveVotes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(votes);
        } catch (IOException e) {
            System.err.println("Error saving votes: " + e.getMessage());
        }
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        saveVotes();
    }

    public List<Vote> getAllVotes() {
        return new ArrayList<>(votes);
    }

    public void clearVotes() {
        votes.clear();
        saveVotes();
    }
}
