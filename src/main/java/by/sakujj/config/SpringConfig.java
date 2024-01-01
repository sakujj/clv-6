package by.sakujj.config;

import by.sakujj.cache.*;
import by.sakujj.connection.ConnectionPool;
import by.sakujj.connection.ConnectionPoolImpl;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dao.ClientDAOImpl;
import by.sakujj.pdf.ReportConfig;
import by.sakujj.proxy.DynamicProxyCreator;
import by.sakujj.servlet.util.InstantAdapter;
import by.sakujj.util.SpringConfigUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

@ComponentScan("by.sakujj")
@Configuration(proxyBeanMethods = false)
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yaml")
public class SpringConfig {

    @Autowired
    private ConfigurableEnvironment env;

    @Bean
    public CacheConfig cacheConfig() {
        Properties properties = SpringConfigUtil.getPropertiesWithPrefixRemoved("dao.cache", env);
        return new CacheConfig(properties);
    }

    @Bean
    public Cache cache(CacheConfig cacheConfig) {
        int capacity = cacheConfig.getCapacity();
        return switch (cacheConfig.getType()) {
            case LFU -> new LFUCache(capacity);
            case LRU -> new LRUCache(capacity);
            case NONE -> new NoOpCache();
        };
    }

    @Bean
    public ClientDAO clientDAOImpl(Cache cache, CacheConfig cacheConfig) {
        if (cacheConfig.getType() == CacheConfig.CacheType.NONE) {
            return new ClientDAOImpl();
        }

        return (ClientDAO) DynamicProxyCreator.newDAOCachingProxy(new ClientDAOImpl(), cache);
    }

    @Bean(destroyMethod = "close")
    public ConnectionPool connectionPoolImpl(ResourceLoader resourceLoader) throws FileNotFoundException {
        Properties hikariProperties = SpringConfigUtil.getPropertiesWithPrefixRemoved("dataSource.hikari", env);
        ConnectionPool connectionPool = new ConnectionPoolImpl(hikariProperties);

        List<String> scriptFilePaths = SpringConfigUtil.getListPropertyValues("dataSource.executeOnStartUp", env);
        SpringConfigUtil.executeScripts(scriptFilePaths, resourceLoader, connectionPool);

        return connectionPool;
    }


    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Bean
    public Validator validator() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        return validator;
    }

    @Bean
    public ReportConfig reportConfig() {
        final String BOLD_FONT_PATH = "pdf-resources/fonts/Hack-Bold.ttf";
        final String REGULAR_FONT_PATH = "pdf-resources/fonts/Hack-Regular.ttf";
        final float RIGHT_MARGIN_TO_WIDTH_RATIO = 1 / 8f;
        final float LEFT_MARGIN_TO_WIDTH_RATIO = 1 / 8f;
        final float TOP_MARGIN_TO_WIDTH_RATIO = 1 / 6f;
        final float BOTTOM_MARGIN_TO_WIDTH_RATIO = 1 / 8f;

        return new ReportConfig(
                BOLD_FONT_PATH,
                REGULAR_FONT_PATH
        ).setTopMarginToHeightRatio(TOP_MARGIN_TO_WIDTH_RATIO)
                .setRightMarginToWidthRatio(RIGHT_MARGIN_TO_WIDTH_RATIO)
                .setLeftMarginToWidthRatio(LEFT_MARGIN_TO_WIDTH_RATIO)
                .setBottomMarginToHeightRatio(BOTTOM_MARGIN_TO_WIDTH_RATIO);
    }
}

