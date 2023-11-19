package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DAO<T, K> {
    Optional<T> findById(K id, Connection connection) throws DAOException;
    List<T> findAll(Connection connection) throws DAOException;
    K save(T obj, Connection connection) throws DAOException;
    boolean update(T obj, Connection connection) throws DAOException;
    boolean deleteById(K id, Connection connection) throws DAOException;
}
