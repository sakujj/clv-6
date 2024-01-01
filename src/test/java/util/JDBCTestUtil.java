package util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@UtilityClass
public class JDBCTestUtil {
    public void executeSQLScriptUsingJDBC(ResourceLoader loader, Connection connection, String scriptFilePath) {
        try {
            List<String> lines = Files.readAllLines(loader.getResource(scriptFilePath).getFile().toPath());
            String query = String.join("\n", lines);

            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
