package election.repository;

import election.model.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<String, User> users = new HashMap<>();
    private final String filePath;

    public UserRepository(String filePath) {
        this.filePath = filePath;
        new File("data").mkdirs();
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String id = parts[0];
                String name = parts[1];
                String type = parts[4];
                User user = "Admin".equals(type) ? new Admin(id, name, "dummy") : new Voter(id, name, "dummy");
                users.put(id, user);
            }
        } catch (Exception e) {
            // Default Admin
            users.put("admin1", new Admin("admin1", "System Administrator", "admin123"));
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (User user : users.values()) {
                writer.println(user.getId() + ":" + user.getName() + ":" +
                        user.getHashedPassword() + ":" + user.getSalt() + ":" +
                        (user instanceof Admin ? "Admin" : "Voter"));
            }
        } catch (IOException e) {
            System.err.println("Error saving users");
        }
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        saveUsers();
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public boolean userExists(String id) {
        return users.containsKey(id);
    }
}
