package integration.dao;

import by.sakujj.dao.ClientDAO;
import by.sakujj.dao.ClientDAOImpl;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Client;
import integration.connection.AbstractConnectionRelatedTests;
import integration.connection.Rollback;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.ClientTestBuilder;
import util.JDBCTestUtil;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Testcontainers
public class ClientDAOImplTests extends AbstractConnectionRelatedTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.4-alpine3.18"
    ).withDatabaseName("sakujj_db_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void setJdbcUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("dataSource.hikari.jdbcUrl", postgres::getJdbcUrl);
    }

    @Autowired
    private ResourceLoader loader;

    private static final String INSERT_TEST_DATA_SCRIPT_PATH = "database-init/dml.sql";
    private static final ClientDAO clientDAO = new ClientDAOImpl();

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldContainAllSpecified(List<Client> expectedToBeContained) throws DAOException, IOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            List<Client> all = clientDAO.findAll(connection);

            // then
            assertThat(all).containsAll(expectedToBeContained);


        }

        static Stream<List<Client>> shouldContainAllSpecified() {
            return Stream.of(List.of(
                    Client.builder()
                            .id(UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"))
                            .username("user0")
                            .email("email0@gmail.com")
                            .password("$2a$10$1mlM3e40rVQ8311QWex89Ozvy91BsmyVuM.bDbCcOjJJIUXMFpcMy")
                            .age(20)
                            .build(),
                    Client.builder()
                            .id(UUID.fromString("b47509ff-44a4-439d-90d8-849619bdb12e"))
                            .username("user1")
                            .email("email1@gmail.com")
                            .password("$2a$10$ZTRludzpodYFUPbeuQwDUuh/E1fLIZhupn9Ql4Fg755NGTOUDqTnm")
                            .age(21)
                            .build(),
                    Client.builder()
                            .id(UUID.fromString("99c0ae88-7acd-4cda-9d59-50e09eede81f"))
                            .username("user2")
                            .email("email2@gmail.com")
                            .password("$2a$10$N5LOdIiZE2H9Buje5sC6kuyV5i8vhw6yyN9jbPTXfpHkP/g7hIZ0W")
                            .age(22)
                            .build(),
                    Client.builder()
                            .id(UUID.fromString("5f8cdb7e-4390-4d6a-bc5c-c4089715d75b"))
                            .username("user3")
                            .email("email3@gmail.com")
                            .password("$2a$10$7hQ2f0E3bkY99MIKpLfAR.AUAFJEk9yoeFgi.ITSicahORaaCkF.y")
                            .age(33)
                            .build()
            ));
        }
    }

    @Nested
    @DisplayName("findById (UUID, Connection)")
    class findById {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            Optional<Client> actual = clientDAO.findById(expected.getId(), connection);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @Test
        @Rollback
        void shouldReturnOptionalEmpty() throws DAOException {
            // given
            Connection connection = getConnection();
//            executeSQLScript(connection, INSERT_TEST_DATA_SCRIPT_PATH);

            UUID id = UUID.fromString("279899b1-8e99-4eef-896a-c68d2357b98b");

            // when
            Optional<Client> actual = clientDAO.findById(id, connection);

            // then
            assertThat(actual).isEmpty();
        }

    }

    @Nested
    @DisplayName("findByEmail (String, Connection)")
    class findByEmail {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            Optional<Client> actual = clientDAO.findByEmail(expected.getEmail(), connection);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @Test
        @Rollback
        void shouldReturnOptionalEmpty() throws DAOException {
            // given
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            String absentEmail = ClientTestBuilder.aClient().getEmail();

            // when
            Optional<Client> actual = clientDAO.findByEmail(absentEmail, connection);

            // then
            assertThat(actual).isEmpty();
        }

    }

    @Nested
    @DisplayName("save (Client)")
    class save {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSaveClient(Client clientToSave) throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            UUID id = clientDAO.save(clientToSave, connection);
            clientToSave.setId(id);
            Optional<Client> actual = clientDAO.findById(id, connection);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(clientToSave);
        }

        static Stream<Client> shouldSaveClient() {
            return findById.shouldReturnCorrectClient()
                    .peek(client -> {
                                client.setEmail(
                                        client.getEmail()
                                                .replace(".", "TEST."));
                                client.setPassword("TEST" + client.getPassword().substring(4));
                            }
                    );
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotSaveWithAlreadyTakenEmail(Client clientWithEmailAlreadyTaken) {
            // given, when, then
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            assertThatThrownBy(() ->
                    clientDAO.save(clientWithEmailAlreadyTaken, connection)
            ).isInstanceOf(DAOException.class);
        }

        static Stream<Client> shouldNotSaveWithAlreadyTakenEmail() {
            return findById.shouldReturnCorrectClient().limit(3);
        }
    }

    @Nested
    @DisplayName("update (Client, Connection)")
    class update {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldUpdateClient(Client clientToUpdate) throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            clientDAO.update(clientToUpdate, getConnection());
            Optional<Client> actual = clientDAO.findById(clientToUpdate.getId(), connection);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(clientToUpdate);
        }

        static Stream<Client> shouldUpdateClient() {
            return findById.shouldReturnCorrectClient().peek(
                    client -> {
                        client.setUsername("UPDATE-TEST %s".formatted(client.getUsername()));
                        client.setEmail("UPDATE-TEST %s".formatted(client.getEmail()));
                        client.setPassword("UPDT%s".formatted(client.getPassword().substring(4)));
                    }
            );
        }

        @Rollback
        @Test
        void shouldNotUpdateToAlreadyTakenEmail() throws DAOException {
            // given
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            Client clientToUpdate = clientDAO.findById(UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"), getConnection()).get();
            String alreadyTakenEmail = "email2@gmail.com";
            clientToUpdate.setEmail(alreadyTakenEmail);

            // when, then
            assertThatThrownBy(() ->
                    clientDAO.update(clientToUpdate, connection)
            ).isInstanceOf(DAOException.class);
        }

        @Rollback
        @Test
        void shouldNotUpdateNonExisting() throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            boolean isUpdated = clientDAO.update(ClientTestBuilder.aClient().build(), connection);

            // then
            assertThat(isUpdated).isFalse();
        }

    }

    @Nested
    @DisplayName("deleteById (UUID, Connection)")
    class deleteById {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldDeleteClient(Client clientToDelete) throws DAOException {
            // given, when
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            clientDAO.deleteById(clientToDelete.getId(), getConnection());
            Optional<Client> empty = clientDAO.findById(clientToDelete.getId(), connection);

            // then
            assertThat(empty).isEmpty();
        }

        static Stream<Client> shouldDeleteClient() {
            return findById.shouldReturnCorrectClient().limit(3);
        }

        @Rollback
        @Test
        void shouldNotDeleteNonExisting() throws DAOException {
            // given
            Connection connection = getConnection();
            JDBCTestUtil.executeSQLScriptUsingJDBC(loader, connection, INSERT_TEST_DATA_SCRIPT_PATH);

            UUID nonExistingId = ClientTestBuilder.aClient().getId();

            // when
            boolean isDeleted = clientDAO.deleteById(nonExistingId, connection);

            // then
            assertThat(isDeleted).isFalse();
        }
    }
}
