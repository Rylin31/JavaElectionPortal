package election.model;

import election.security.PasswordHasher;
// import election.SecureElectionPortal;

public abstract class User { //  ADD "public" HERE!
    private final String id, name, hashedPassword, salt;

    protected User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.salt = PasswordHasher.generateSalt();
        this.hashedPassword = PasswordHasher.hashPassword(salt, password);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean authenticate(String password) {
        return PasswordHasher.verifyPassword(salt, password, hashedPassword);
    }

    public abstract void displayMenu(election.SecureElectionPortal portal);

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s", id, name);
    }
}

