package by.sakujj.connection;

import by.sakujj.exceptions.DAOException;

import java.sql.Connection;

public interface ConnectionPool extends AutoCloseable {
    Connection getConnection() throws DAOException;
}
