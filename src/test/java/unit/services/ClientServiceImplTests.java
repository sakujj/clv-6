package unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import by.sakujj.services.impl.ClientServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ClientTestBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTests {
    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;


    @Nested
    @DisplayName("findById (UUID)")
    public class findById {
        @Test
        void shouldReturnCorrectResponse() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Client expectedClient = aClient.build();
            Optional<ClientResponse> expected = Optional.of(aClient.buildResponse());

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findById(expectedClient.getId(), connection))
                    .thenReturn(Optional.of(expectedClient));
            Mockito.when(clientMapper.toResponse(expectedClient))
                    .thenReturn(expected.get());

            // when
            Optional<ClientResponse> actual = clientServiceImpl.findById(expectedClient.getId());

            // then
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expected);
        }


        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            UUID id = ClientTestBuilder.aClient().getId();

            // when
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // then
            assertThatThrownBy(() -> clientServiceImpl.findById(id))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            UUID id = ClientTestBuilder.aClient().getId();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findById(id, connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findById(id))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findById(ClientTestBuilder.aClient().getId()))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("findByEmail (String)")
    public class findByEmail {
        @Test
        void shouldReturnCorrectResponse() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Optional<ClientResponse> expected = Optional.of(aClient.buildResponse());
            Client expectedClient = aClient.build();

            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(clientDAO.findByEmail(aClient.getEmail(), connection))
                    .thenReturn(Optional.of(expectedClient));
            Mockito.when(clientMapper.toResponse(expectedClient))
                    .thenReturn(expected.get());

            // when
            Optional<ClientResponse> actual = clientServiceImpl.findByEmail(aClient.getEmail());

            // then
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            String email = ClientTestBuilder.aClient().getEmail();

            // when
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // then
            assertThatThrownBy(() -> clientServiceImpl.findByEmail(email))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            String email = ClientTestBuilder.aClient().getEmail();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findByEmail(email, connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findByEmail(email))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findByEmail(ClientTestBuilder.aClient().getEmail()))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("findAll ()")
    public class findAll {
        @Test
        void shouldReturnAllBanks() throws DAOException {
            // given
            ClientTestBuilder aClient1 = ClientTestBuilder.aClient();
            ClientTestBuilder aClient2 = ClientTestBuilder.aClient()
                    .withId(UUID.fromString("f14975ae-5741-4727-9532-87cb1fc17c8c"))
                    .withAge(35)
                    .withEmail("email2")
                    .withUsername("username2")
                    .withPassword("2")
                    .withNotHashedPassword("n2");
            ClientTestBuilder aClient3 = ClientTestBuilder.aClient()
                    .withId(UUID.fromString("c4e0a0e8-9d8d-45a3-8248-7fd74b366962"))
                    .withAge(39)
                    .withEmail("email3")
                    .withUsername("username3")
                    .withPassword("3")
                    .withNotHashedPassword("n3");

            List<Client> expectedFromDAO = List.of(
                    aClient1.build(),
                    aClient2.build(),
                    aClient3.build());
            List<ClientResponse> expected = List.of(
                    aClient1.buildResponse(),
                    aClient2.buildResponse(),
                    aClient3.buildResponse());

            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(clientDAO.findAll(connection))
                    .thenReturn(expectedFromDAO);
            for (int i = 0; i < expected.size(); i++) {
                Mockito.when(clientMapper.toResponse(expectedFromDAO.get(i)))
                        .thenReturn(expected.get(i));
            }

            // when
            List<ClientResponse> actual = clientServiceImpl.findAll();

            // then
            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findAll())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findAll(connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findAll())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.findAll())
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("save (ClientRequest)")
    public class save {
        @Test
        void shouldSave() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientRequest clientRequest = aClient.buildRequest();
            Client client = aClient.build();
            UUID expected = client.getId();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientMapper.fromRequest(clientRequest))
                    .thenReturn(client);
            Mockito.when(clientDAO.save(client, connection))
                    .thenReturn(expected);

            // when
            UUID actual = clientServiceImpl.save(clientRequest);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.save(ClientRequest.builder().build()))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Client client = aClient.build();
            ClientRequest clientRequest = aClient.buildRequest();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientMapper.fromRequest(clientRequest))
                    .thenReturn(client);
            Mockito.when(clientDAO.save(client, connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.save(clientRequest))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.save(ClientTestBuilder.aClient().buildRequest()))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("update (ClientRequest)")
    public class update {
        @Test
        void shouldUpdate() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            ClientRequest clientRequest = aClient.buildRequest();
            UUID clientId = aClient.getId();
            Client client = aClient.build();
            boolean expected = true;

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientMapper.fromRequest(clientRequest))
                    .thenReturn(client);
            Mockito.when(clientDAO.findById(clientId, connection))
                            .thenReturn(Optional.of(client));
            Mockito.when(clientDAO.update(client, connection))
                    .thenReturn(expected);

            // when
            boolean actual = clientServiceImpl.updateById(clientId, clientRequest);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.updateById(aClient.getId(), aClient.buildRequest()))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Client client = aClient.build();
            ClientRequest clientRequest = aClient.buildRequest();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientMapper.fromRequest(clientRequest))
                    .thenReturn(client);
            Mockito.when(clientDAO.findById(client.getId(), connection))
                    .thenReturn(Optional.of(client));
            Mockito.when(clientDAO.update(client, connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.updateById(client.getId(),clientRequest))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.updateById(aClient.getId(), aClient.buildRequest()))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("deleteById (UUID)")
    public class deleteById {
        @Test
        void shouldReturnCorrectResponse() throws DAOException {
            // given
            ClientTestBuilder aClient = ClientTestBuilder.aClient();
            Client client = aClient.build();
            UUID clientId = client.getId();
            boolean expected = true;

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findById(clientId, connection))
                    .thenReturn(Optional.of(client));
            Mockito.when(clientDAO.deleteById(clientId, connection))
                    .thenReturn(expected);

            // when
            boolean actual = clientServiceImpl.deleteById(clientId);

            // then
            assertThat(actual).isEqualTo(expected);
        }


        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromConnectionPool() throws DAOException {
            // given
            UUID id = ClientTestBuilder.aClient().getId();

            // when
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new DAOException());

            // then
            assertThatThrownBy(() -> clientServiceImpl.deleteById(id))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnDAOExceptionFromDAO() throws DAOException {
            // given
            Client aClient = ClientTestBuilder.aClient().build();
            UUID id = aClient.getId();

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findById(id, connection))
                    .thenReturn(Optional.of(aClient));
            Mockito.when(clientDAO.deleteById(id, connection))
                    .thenThrow(new DAOException());

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.deleteById(id))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void shouldThrowRuntimeExceptionOnSQLExceptionFromClosingConnection() throws DAOException, SQLException {
            // given
            doThrow(SQLException.class).when(connection).close();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);

            // when, then
            assertThatThrownBy(() -> clientServiceImpl.deleteById(ClientTestBuilder.aClient().getId()))
                    .isInstanceOf(RuntimeException.class);
        }
    }

}
