package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Client;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientDAO extends DAO<Client, UUID> {
    Optional<Client> findByEmail(String email, Connection connection) throws DAOException;
}
