package election.repository;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class AuditLogRepository {
    private final LinkedList<String> logs = new LinkedList<>();
    private final String filePath;

    public AuditLogRepository(String filePath) {
        this.filePath = filePath;
        loadLogs();
    }

    private void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            System.out.println("Initializing empty audit log");
        }
    }

    private void saveLogs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving audit logs: " + e.getMessage());
        }
    }

    public void logAction(String action) {
        String logEntry = String.format("%s: %s",
                java.time.LocalDateTime.now(), action);
        logs.add(logEntry);
        if (logs.size() > 1000)
            logs.removeFirst(); // Keep last 1000
        saveLogs();
    }

    public List<String> getAllLogs() {
        return new LinkedList<>(logs);
    }
}
