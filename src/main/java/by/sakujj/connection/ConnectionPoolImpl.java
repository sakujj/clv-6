package by.sakujj.connection;

import by.sakujj.exceptions.DAOException;
import by.sakujj.util.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;



@Slf4j
public class ConnectionPoolImpl implements ConnectionPool {
    private final HikariDataSource dataSource;

    /**
     * @param propertiesFileName fileName to get configuration properties from
     */
    public ConnectionPoolImpl(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
        Properties properties = PropertiesUtil.newPropertiesFromYaml("dataSource", propertiesFileName);
        dataSource = newHikariDataSource(properties);
    }

    private final String propertiesFileName;

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
    public Connection getConnection() throws DAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Used to close pool when application terminates
     */
    @Override
    public void close() {
        dataSource.close();
        log.info("CP associated with '%s' has been closed".formatted(propertiesFileName));
    }
}
