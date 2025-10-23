package election.model;

public class Candidate { //  ADD "public" HERE!
    private final String id;
    private final String name;
    private final String party;
    private int votes;

    public Candidate(String id, String name, String party) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.votes = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public int getVotes() {
        return votes;
    }

    public void incrementVotes() {
        votes++;
    }

    public void resetVotes() {
        votes = 0;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | %s (%s) | Votes: %d", id, name, party, votes);
    }
}

