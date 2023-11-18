package by.sakujj.connection;

import by.sakujj.util.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

;


@Slf4j
public class ConnectionPoolImpl implements ConnectionPool{
    private final HikariDataSource dataSource;

    public ConnectionPoolImpl(String propertiesFileName){
        this.propertiesFileName = propertiesFileName;
        Properties properties = PropertiesUtil.newPropertiesFromYaml("dataSource", propertiesFileName);
        dataSource = newHikariDataSource(properties);
    }

    private final String propertiesFileName;

    @SneakyThrows
    private static HikariDataSource newHikariDataSource(Properties properties) {
        HikariConfig hikariConfig = new HikariConfig(properties);
        System.out.println(hikariConfig.getDataSourceProperties());

        return new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        dataSource.close();
        log.info("CP associated with '%s' has been closed".formatted(propertiesFileName));
    }
}
