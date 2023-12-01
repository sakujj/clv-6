package by.sakujj.services.impl;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.exceptions.ConnectionPoolException;
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

    /**
     * Used to save a client to DB. ID will be automatically generated.
     *
     * @param request {@link ClientRequest} to get the client info
     * @return {@code id} that was generated for the client
     */
    public UUID save(ClientRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            Client client = clientMapper.fromRequest(request);
            return clientDAO.save(client, connection);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to update a client from DB by id, using info from {@link ClientRequest} request.
     * All fields of the client will be updated, except for {@code id}.
     *
     * @param id id
     * @param request {@link ClientRequest} to get update info
     * @return {@code true} if deletion was successful, {@code false} otherwise
     */
    public boolean updateById(UUID id, ClientRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            if (clientDAO.findById(id, connection).isEmpty()) {
                return false;
            }

            Client client = clientMapper.fromRequest(request);
            client.setId(id);
            return clientDAO.update(client, connection);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to retrieve ClientResponses of all present clients.
     *
     * @return {@link ClientResponse} list of present clients
     */
    public List<ClientResponse> findAll() {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findAll(connection)
                    .stream()
                    .map(clientMapper::toResponse)
                    .toList();
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to retrieve ClientResponse by id.
     *
     * @param id id
     * @return Optional with {@link ClientResponse} if client with id is present,
     * {@code Optional.empty()} otherwise
     */
    public Optional<ClientResponse> findById(UUID id) {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findById(id, connection)
                    .map(clientMapper::toResponse);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to retrieve ClientResponse by email.
     *
     * @param email email
     * @return Optional with {@link ClientResponse} if client with email is present,
     * {@code Optional.empty()} otherwise
     */
    public Optional<ClientResponse> findByEmail(String email) {
        try (Connection connection = connectionPool.getConnection()) {
            return clientDAO.findByEmail(email, connection)
                    .map(clientMapper::toResponse);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Used to delete Client from DB by id.
     *
     * @param id id
     * @return {@code true} if deletion was successful, {@code false} otherwise
     */
    public boolean deleteById(UUID id) {
        try (Connection connection = connectionPool.getConnection()) {
            if (clientDAO.findById(id, connection).isEmpty()) {
                return false;
            }

            return clientDAO.deleteById(id, connection);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }
}
