package election.model;

public class Voter extends User { //  ADD "public" HERE!
    private boolean hasVoted = false;

    public Voter(String id, String name, String password) {
        super(id, name, password);
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public void displayMenu(election.SecureElectionPortal portal) {
        portal.showVoterMenu(this);
    }
}

