package unit.hashing;

import by.sakujj.context.Context;
import by.sakujj.hashing.Hasher;
import integration.context.TestContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;


public class BCryptHasherTests {
    private static final Context context = TestContext.getInstance();

    Hasher hasher = context.getByClass(Hasher.class);

    @ParameterizedTest
    @ValueSource(strings = {"pass1", "qwertyzxcv32432@@", "s"})
    void shouldHashAndThenVerify(String password) {
        String hashed = hasher.hash(password);
        boolean isVerified = hasher.verifyHash(password, hashed);

        assertThat(isVerified).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"pass1", "qwertyzxcv32432@@", "s"})
    void differentPasswordsHaveDifferentHashes(String password) {
        String hashed = hasher.hash(password);
        boolean isVerified = hasher.verifyHash(password + "test", hashed);

        assertThat(isVerified).isFalse();
    }


    @ParameterizedTest
    @ValueSource(strings = {"pass1", "qwertyzxcv32432@@", "s"})
    void samePasswordsHaveDifferentHashes(String password) {
        String hashedFirst = hasher.hash(password);
        String hashedSecond = hasher.hash(password);

        assertThat(hashedFirst).isNotEqualTo(hashedSecond);
    }
}
