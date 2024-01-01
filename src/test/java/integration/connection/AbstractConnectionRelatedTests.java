package integration.connection;

import by.sakujj.config.SpringConfig;
import by.sakujj.connection.ConnectionPool;
import by.sakujj.exceptions.DAOException;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class AbstractConnectionRelatedTests {
    @Autowired
    private ConnectionPool connectionPool;

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
