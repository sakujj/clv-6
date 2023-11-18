package by.sakujj.hashing;

public interface Hasher {
    String hash(String plainText);

    boolean verifyHash(String candidateText, String storedHash);
}
