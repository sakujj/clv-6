package by.sakujj.dao.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SQLQueries {
    public static String getSelectByAttribute(String tableName, String attributeName) {
        return """
                SELECT *
                    FROM %s
                WHERE %s = ?;
                """.formatted(tableName, attributeName);
    }

    public static String getSelectAll(String tableName) {
        return """
                SELECT *
                    FROM %s;
                """.formatted(tableName);
    }

    public static String getUpdateByAttribute(String tableName, String attributeName,
                                              List<String> attributesToUpdate) {
        return """
                UPDATE %s
                \tSET
                %s
                WHERE %s = ?;
                """.formatted(
                tableName,
                attributesToUpdate
                        .stream()
                        .map("\t%s = ?"::formatted)
                        .collect(Collectors.joining(",\n")),
                attributeName);

    }

    public static String getDeleteByAttribute(String tableName, String attributeName) {
        return """
                DELETE FROM %s
                WHERE %s = ?;
                """.formatted(tableName, attributeName);
    }

    public static String getInsert(String tableName, List<String> attributes) {
        return """
                INSERT INTO %s
                    (%s)
                VALUES
                    (%s);
                """.formatted(
                tableName,
                String.join(", ", attributes),
                attributes
                        .stream()
                        .map(a -> "?")
                        .collect(Collectors.joining(", ")));
    }
}
