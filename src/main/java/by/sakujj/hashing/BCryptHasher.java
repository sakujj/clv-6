package by.sakujj.hashing;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@RequiredArgsConstructor
public class BCryptHasher implements Hasher{
    private final static int LOG_ROUNDS = 10;

    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    @Override
    public boolean verifyHash(String candidatePassword, String storedHash) {
        return BCrypt.checkpw(candidatePassword, storedHash);
    }
}
