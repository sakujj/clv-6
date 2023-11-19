package integration.dao;

import by.sakujj.context.Context;
import by.sakujj.dao.ClientDAO;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Client;
import integration.connection.AbstractConnectionRelatedTests;
import integration.connection.Rollback;
import integration.context.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.ClientTestBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class ClientDAOTests extends AbstractConnectionRelatedTests {
    private static final Context context = TestContext.getInstance();

    private static final ClientDAO clientDAO = context.getByClass(ClientDAO.class);

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @ParameterizedTest
        @MethodSource
        void shouldContainAllSpecified(List<Client> expectedToBeContained) throws DAOException {
            // given, when
            List<Client> all = clientDAO.findAll(getConnection());

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

        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            // given, when
            Optional<Client> actual = clientDAO.findById(expected.getId(), getConnection());

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @Test
        void shouldReturnOptionalEmpty() throws DAOException {
            // given
            UUID id = UUID.fromString("279899b1-8e99-4eef-896a-c68d2357b98b");

            // when
            Optional<Client> actual = clientDAO.findById(id, getConnection());

            // then
            assertThat(actual).isEmpty();
        }

    }

    @Nested
    @DisplayName("findByEmail (String, Connection)")
    class findByEmail {

        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            // given, when
            Optional<Client> actual = clientDAO.findByEmail(expected.getEmail(), getConnection());

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @Test
        void shouldReturnOptionalEmpty() throws DAOException {
            // given
            String absentEmail = ClientTestBuilder.aClient().getEmail();

            // when
            Optional<Client> actual = clientDAO.findByEmail(absentEmail, getConnection());

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
            UUID id = clientDAO.save(clientToSave, getConnection());
            clientToSave.setId(id);
            Optional<Client> actual = clientDAO.findById(id, getConnection());

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
            assertThatThrownBy(() ->
                    clientDAO.save(clientWithEmailAlreadyTaken, getConnection())
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
            clientDAO.update(clientToUpdate, getConnection());
            Optional<Client> actual = clientDAO.findById(clientToUpdate.getId(), getConnection());

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
            Client clientToUpdate = clientDAO.findById(UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"), getConnection()).get();
            String alreadyTakenEmail = "email2@gmail.com";
            clientToUpdate.setEmail(alreadyTakenEmail);

            // when, then
            assertThatThrownBy(() ->
                    clientDAO.update(clientToUpdate, getConnection())
            ).isInstanceOf(DAOException.class);

        }

        @Rollback
        @Test
        void shouldNotUpdateNonExisting() throws DAOException {
            // given, when
            boolean isUpdated = clientDAO.update(ClientTestBuilder.aClient().build(), getConnection());

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
            clientDAO.deleteById(clientToDelete.getId(), getConnection());
            Optional<Client> empty = clientDAO.findById(clientToDelete.getId(), getConnection());

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
            UUID nonExistingId = ClientTestBuilder.aClient().getId();

            // when
            boolean isDeleted = clientDAO.deleteById(nonExistingId, getConnection());

            // then
            assertThat(isDeleted).isFalse();
        }
    }
}
