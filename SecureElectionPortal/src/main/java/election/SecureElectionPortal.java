package election;

import election.model.*;
import election.repository.*;
import election.service.*;
import java.util.Scanner;

public class SecureElectionPortal {
    private final UserRepository userRepo;
    private final CandidateRepository candidateRepo;
    private final VoteRepository voteRepo;
    private final AuditLogRepository auditRepo;
    private final AdminService adminService;
    private final VoterService voterService;
    private final ElectionService electionService;

    public SecureElectionPortal() {
        String dataDir = "data/";
        userRepo = new UserRepository(dataDir + "users.dat");
        candidateRepo = new CandidateRepository(dataDir + "candidates.dat");
        voteRepo = new VoteRepository(dataDir + "votes.dat");
        auditRepo = new AuditLogRepository(dataDir + "audit_logs.txt");

        adminService = new AdminService(userRepo, candidateRepo, auditRepo);
        voterService = new VoterService(voteRepo, candidateRepo, auditRepo);
        electionService = new ElectionService(voteRepo, candidateRepo);

        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        if (!userRepo.userExists("admin1")) {
            Admin admin = new Admin("admin1", "System Administrator", "admin123");
            userRepo.addUser(admin);
            auditRepo.logAction("Default admin created");
        }
    }

    public static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println(" Invalid input. Enter a number.");
            scanner.next();
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    // ADMIN MENUS
    public void showAdminMenu(Admin admin) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n  ADMIN DASHBOARD  ");
            System.out.println("1.  Add Candidate");
            System.out.println("2.  Register Voter");
            System.out.println("3.    Start Election");
            System.out.println("4.   End Election");
            System.out.println("5.   View Audit Logs");
            System.out.println("6.   View Results");
            System.out.println("7.  Logout");

            int choice = getIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        addCandidate(scanner);
                        break;
                    case 2:
                        registerVoter(scanner);
                        break;
                    case 3:
                        startElection();
                        break;
                    case 4:
                        endElection();
                        break;
                    case 5:
                        displayAuditLogs();
                        break;
                    case 6:
                        displayResults();
                        break;
                    case 7:
                        return;
                }
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }
    }

    // VOTER MENUS
    public void showVoterMenu(Voter voter) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n   VOTER PORTAL  ");
            System.out.println("1.   View Candidates");
            System.out.println("2.   Cast Vote");
            System.out.println("3.   View Results");
            System.out.println("4.  Logout");

            int choice = getIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        displayCandidates();
                        break;
                    case 2:
                        castVote(voter, scanner);
                        break;
                    case 3:
                        displayResults();
                        break;
                    case 4:
                        return;
                }
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }
    }

    // ADMIN OPERATIONS
    private void addCandidate(Scanner scanner) {
        if (electionService.isElectionStarted()) {
            System.out.println(" Cannot add candidates after election started!");
            return;
        }
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Party: ");
        String party = scanner.nextLine();
        adminService.addCandidate(id, name, party);
        System.out.println("  Candidate added successfully!");
    }

    private void registerVoter(Scanner scanner) {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        adminService.registerVoter(id, name, password);
        System.out.println("  Voter registered successfully!");
    }

    private void startElection() {
        electionService.startElection();
        auditRepo.logAction("Election started by Admin");
        System.out.println("  Election started successfully!");
    }

    private void endElection() {
        electionService.endElection();
        auditRepo.logAction("Election ended by Admin");
        System.out.println("  Election ended successfully!");
    }

    // VOTER OPERATIONS
    private void castVote(Voter voter, Scanner scanner) {
        if (voter.hasVoted()) {
            System.out.println(" You have already voted!");
            return;
        }
        if (!electionService.isElectionStarted() || electionService.isElectionEnded()) {
            System.out.println(" Election is not active!");
            return;
        }

        displayCandidates();
        System.out.print("Enter Candidate ID: ");
        String candidateId = scanner.nextLine();
        voterService.castVote(voter, candidateId);
        System.out.println("  Vote cast successfully! Thank you for voting!  ");
    }

    // DISPLAY METHODS
    private void displayCandidates() {
        var candidates = candidateRepo.getAllCandidates();
        if (candidates.isEmpty()) {
            System.out.println(" No candidates available.");
            return;
        }
        System.out.println("\n  CANDIDATES LIST:");
        System.out.println("=".repeat(50));
        candidates.forEach(System.out::println);
        System.out.println("=".repeat(50));
    }

    private void displayResults() {
        if (!electionService.isElectionEnded()) {
            System.out.println(" Results available only after election ends.");
            return;
        }
        System.out.println("\n  ELECTION RESULTS:");
        System.out.println("=".repeat(50));
        candidateRepo.getAllCandidates().forEach(System.out::println);
        Candidate winner = electionService.getWinner();
        if (winner != null) {
            System.out.println("\n WINNER: " + winner.getName() +
                    " (" + winner.getParty() + ") with " +
                    winner.getVotes() + " votes!");
        }
        System.out.println("=".repeat(50));
    }

    private void displayAuditLogs() {
        System.out.println("\n  AUDIT LOGS (Last 50 entries):");
        System.out.println("=".repeat(80));
        auditRepo.getAllLogs().stream()
                .limit(50)
                .forEach(System.out::println);
        System.out.println("=".repeat(80));
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("  Enter ID: ");
        String id = scanner.nextLine();
        System.out.print("   Enter Password: ");
        String password = scanner.nextLine();

        User user = userRepo.getUser(id);
        if (user != null && user.authenticate(password)) {
            auditRepo.logAction("Login successful: " + user.getName());
            System.out.println("\n  Welcome, " + user.getName() + "!");
            user.displayMenu(this);
            auditRepo.logAction("Logout: " + user.getName());
        } else {
            System.out.println(" Invalid credentials!");
        }
    }

    public static void main(String[] args) {
        System.out.println(" SECURE ELECTION PORTAL v2.0 ");
        System.out.println("=".repeat(50));

        SecureElectionPortal portal = new SecureElectionPortal();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n  MAIN MENU ");
            System.out.println("1.   Login");
            System.out.println("2.  Exit");
            int choice = getIntInput(scanner, "Enter choice: ");

            if (choice == 1) {
                portal.login();
            } else if (choice == 2) {
                System.out.println("  Thank you for using Secure Election Portal!");
                break;
            }
        }
    }
}

