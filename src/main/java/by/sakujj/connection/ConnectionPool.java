package by.sakujj.connection;

import by.sakujj.exceptions.DAOException;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool extends AutoCloseable {
    Connection getConnection() throws SQLException, DAOException;
}
