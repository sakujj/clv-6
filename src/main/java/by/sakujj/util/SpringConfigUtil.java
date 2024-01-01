package by.sakujj.util;

import by.sakujj.connection.ConnectionPool;
import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

@UtilityClass
public class SpringConfigUtil {
    public static void executeScripts(List<String> scriptFilePaths, ResourceLoader loader, ConnectionPool connectionPool) {
        try (Connection connection = connectionPool.getConnection()) {
            for (var scriptFilePath : scriptFilePaths) {
                try (Reader reader = new InputStreamReader(loader.getResource(scriptFilePath).getInputStream())) {
                    SQLScriptRunner.runScript(connection, reader);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getListPropertyValues(String listPropertyName, Environment env) {
        String executeOnStartUpProperty = listPropertyName + "[%s]";

        List<String> containedValues = new ArrayList<>();
        String propertyValue = null;
        for (int i =0 ; (propertyValue = env.getProperty(executeOnStartUpProperty.formatted(i))) != null; i++) {
            containedValues.add(propertyValue);
        }

        return containedValues;
    }

    public static Properties getPropertiesWithPrefixRemoved(String prefix, ConfigurableEnvironment env) {
        Properties properties = new Properties();
        if (env instanceof ConfigurableEnvironment) {
            for (var propertySource : env.getPropertySources()) {
                if (propertySource instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                        if (key.startsWith(prefix)) {
                            properties.put(key.replaceFirst(Pattern.quote(prefix + "."), ""), propertySource.getProperty(key));
                        }
                    }
                }
            }
        }

        return properties;
    }
}
