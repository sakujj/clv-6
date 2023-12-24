package by.sakujj.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DAO<T, K> {
    Optional<T> findById(K id, Connection connection);
    List<T> findAll(Connection connection);
    List<T> findByPageWithSize(int page, int size, Connection connection);
    K save(T obj, Connection connection);
    boolean update(T obj, Connection connection);
    boolean deleteById(K id, Connection connection);
}
