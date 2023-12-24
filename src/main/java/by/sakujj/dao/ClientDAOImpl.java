package by.sakujj.dao;

import by.sakujj.annotations.Cacheable;
import by.sakujj.dao.util.SQLQueries;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Client;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientDAOImpl implements ClientDAO {

    private static final String SCHEMA_NAME = "myschema";
    private static final String TABLE_NAME = SCHEMA_NAME + "." + "Client";
    private static final String ID_COLUMN_NAME = "client_id";

    private static final List<String> ATTRIBUTES = List.of(
            "client_id",
            "username",
            "email",
            "password",
            "age"
    );

    private static final List<String> ATTRIBUTES_WITHOUT_ID = List.of(
            "username",
            "email",
            "password",
            "age"
    );

    private static final String FIND_BY_ID = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String FIND_BY_EMAIL = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            "email"
    );

    private static final String FIND_ALL = SQLQueries.getSelectAll(TABLE_NAME);

    private static final String UPDATE_BY_ID = SQLQueries.getUpdateByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    private static final String DELETE_BY_ID = SQLQueries.getDeleteByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String INSERT = SQLQueries.getInsert(
            TABLE_NAME,
            ATTRIBUTES
    );

    @Override
    @Cacheable(daoMethod = Cacheable.DAOMethod.FIND_BY_ID)
    public Optional<Client> findById(UUID id, Connection connection){
        return ClientDAOImpl.findByAttr(FIND_BY_ID, id, connection).stream().findAny();
    }

    public Optional<Client> findByEmail(String email, Connection connection){
        return ClientDAOImpl.findByAttr(FIND_BY_EMAIL, email, connection).stream().findAny();
    }

    @Override
    public List<Client> findAll(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            List<Client> all = new ArrayList<>();
            while (resultSet.next()) {
                all.add(newClient(resultSet));
            }

            log.info("{}\n--------------------------------", statement);

            return all;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Client> findByPageWithSize(int page, int size, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM %s LIMIT %s OFFSET %s;
                """.formatted(TABLE_NAME, String.valueOf(size), String.valueOf((page - 1)* size)))) {

            ResultSet resultSet = statement.executeQuery();

            List<Client> selected = new ArrayList<>();
            while (resultSet.next()) {
                selected.add(newClient(resultSet));
            }

            log.info("{}\n--------------------------------", statement);

            return selected;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    @Cacheable(daoMethod = Cacheable.DAOMethod.SAVE)
    public UUID save(Client obj, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT,
                Statement.RETURN_GENERATED_KEYS)) {

            UUID generatedId = UUID.randomUUID();
            obj.setId(generatedId);

            ClientDAOImpl.initStatement(statement, obj);
            statement.executeUpdate();

            log.info("{}\n--------------------------------", statement);

            return generatedId;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    @Cacheable(daoMethod = Cacheable.DAOMethod.UPDATE)
    public boolean update(Client obj, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID)) {
            ClientDAOImpl.initStatementWithoutId(statement, obj);
            statement.setObject(ATTRIBUTES_WITHOUT_ID.size() + 1, obj.getId());

            log.info("{}\n--------------------------------", statement);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }

    @Override
    @Cacheable(daoMethod = Cacheable.DAOMethod.DELETE_BY_ID)
    public boolean deleteById(UUID id, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {
            statement.setObject(1, id);

            log.info("{}\n--------------------------------", statement);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static void initStatement(PreparedStatement statement, Client obj) throws SQLException {
        statement.setObject(1, obj.getId());
        statement.setObject(2, obj.getUsername());
        statement.setObject(3, obj.getEmail());
        statement.setObject(4, obj.getPassword());
        statement.setObject(5, obj.getAge());
    }

    private static void initStatementWithoutId(PreparedStatement statement, Client obj) throws SQLException {
        statement.setObject(1, obj.getUsername());
        statement.setObject(2, obj.getEmail());
        statement.setObject(3, obj.getPassword());
        statement.setObject(4, obj.getAge());
    }

    public static Client newClient(ResultSet resultSet) throws SQLException {
        UUID id = resultSet.getObject("client_id", UUID.class);
        String username = resultSet.getObject("username", String.class);
        String email = resultSet.getObject("email", String.class);
        String password = resultSet.getObject("password", String.class);
        Integer age = resultSet.getObject("age", Integer.class);

        return Client.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .age(age)
                .build();
    }

    private static <T> List<Client> findByAttr(String queryToFindBy,
                                               T attr,
                                               Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(queryToFindBy)) {
            statement.setObject(1, attr);
            ResultSet resultSet = statement.executeQuery();

            List<Client> clients = new ArrayList<>();
            while (resultSet.next()) {
                clients.add(newClient(resultSet));
            }

            log.info("{}\n--------------------------------", statement);

            return clients;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
}
