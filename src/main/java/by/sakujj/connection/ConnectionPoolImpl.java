package by.sakujj.connection;

import by.sakujj.exceptions.ConnectionPoolException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;



@Slf4j
public class ConnectionPoolImpl implements ConnectionPool {
    private final HikariDataSource dataSource;


    public ConnectionPoolImpl(Properties properties) {
        dataSource = newHikariDataSource(properties);
    }


    private static HikariDataSource newHikariDataSource(Properties properties) {
        HikariConfig hikariConfig = new HikariConfig(properties);

        return new HikariDataSource(hikariConfig);
    }

    /**
     * Gets connection from the pool.
     *
     * @return connection from the pool
     */
    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to close pool when application terminates
     */
    @Override
    public void close() {
        dataSource.close();
        log.info("CP has been closed");
    }
}
