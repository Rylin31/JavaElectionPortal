package election.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
// import java.util.Date;

public class VoteIntegrity {
    private static final String ALGORITHM = "SHA-256";

    public static String generateVoteHash(String voterId, String candidateId, String timestamp) {
        try {
            String data = voterId + candidateId + timestamp;
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = md.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash generation failed", e);
        }
    }

    public static boolean verifyVoteIntegrity(String voterId, String candidateId,
            String timestamp, String storedHash) {
        String currentHash = generateVoteHash(voterId, candidateId, timestamp);
        return currentHash.equals(storedHash);
    }
}
