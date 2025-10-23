package election.model;

import election.SecureElectionPortal;

public class Admin extends User {
    public Admin(String id, String name, String password) {
        super(id, name, password);
    }

    @Override
    public void displayMenu(SecureElectionPortal portal) {
        portal.showAdminMenu(this);
    }
}
