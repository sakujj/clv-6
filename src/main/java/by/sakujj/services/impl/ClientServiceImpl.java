package by.sakujj.services.impl;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import by.sakujj.services.ClientService;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientDAO clientDAO;
    private final ClientMapper clientMapper;
    private final ConnectionPool connectionPool;

    public UUID save(ClientRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            Client client = clientMapper.fromRequest(request);
            return clientDAO.save(client, connection);
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateById(UUID id, ClientRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            if (clientDAO.findById(id, connection).isEmpty()) {
                return false;
            }

            Client client = clientMapper.fromRequest(request);
            client.setId(id);
            return clientDAO.update(client, connection);
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClientResponse> findAll() {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findAll(connection)
                    .stream()
                    .map(clientMapper::toResponse)
                    .toList();
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ClientResponse> findById(UUID id) {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findById(id, connection)
                    .map(clientMapper::toResponse);
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ClientResponse> findByEmail(String email) {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findByEmail(email, connection)
                    .map(clientMapper::toResponse);
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteById(UUID id) {
        try (Connection connection = connectionPool.getConnection()) {
            if (clientDAO.findById(id, connection).isEmpty()) {
                return false;
            }

            return clientDAO.deleteById(id, connection);
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
