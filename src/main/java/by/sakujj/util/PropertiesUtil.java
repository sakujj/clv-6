package by.sakujj.util;

import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@UtilityClass
public class PropertiesUtil {

    public static Properties newPropertiesFromYaml(String prefix, String filename) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = ClassLoader
                .getSystemClassLoader()
                .getResourceAsStream(filename)) {

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

    public static Properties newProperties(String filename) {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}