package by.sakujj.connection;

import java.sql.Connection;

public interface ConnectionPool extends AutoCloseable {
    Connection getConnection();
}
