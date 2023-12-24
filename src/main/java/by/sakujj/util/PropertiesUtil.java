package by.sakujj.util;

import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {

    public static Properties newPropertiesFromYaml(String prefix, String filename, ClassLoader propertyLoader) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = propertyLoader.getResourceAsStream(filename)) {

            Map<Object, Object> propertiesMap = yaml.load(inputStream);
            if (!prefix.isEmpty()) {
                var prefixes = prefix.split("\\.");
                for (String p : prefixes) {
                    propertiesMap = (Map<Object, Object>) propertiesMap.get(p);
                }
            }

            Properties properties = new Properties();
            properties.putAll(propertiesMap);

            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties newProperties(String filename, ClassLoader propertyLoader) {
        try(InputStream inputStream = propertyLoader.getResourceAsStream(filename)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}