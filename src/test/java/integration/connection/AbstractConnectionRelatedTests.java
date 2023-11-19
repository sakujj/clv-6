package integration.connection;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.context.Context;
import by.sakujj.exceptions.DAOException;
import integration.context.TestContext;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractConnectionRelatedTests {
    private static final Context context = TestContext.getInstance();


    private static final ConnectionPool connectionPool
            = context.getByClass(ConnectionPool.class);

    private boolean doRollback = false;

    @Getter
    private Connection connection = null;

    @BeforeEach
    void openConnection(TestInfo testInfo) throws SQLException, DAOException {
        connection = connectionPool.getConnection();

        doRollback = testInfo.getTestMethod().get().getAnnotation(Rollback.class) != null;

        if (doRollback) {
            connection.setAutoCommit(false);
        }
    }


    @AfterEach
    void closeConnection() throws SQLException {
        if (doRollback) {
            connection.rollback();
            connection.setAutoCommit(true);
        }

        connection.close();
    }
}
