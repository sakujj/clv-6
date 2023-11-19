package by.sakujj.context;

import by.sakujj.cache.Cache;
import by.sakujj.cache.CacheConfig;
import by.sakujj.cache.LFUCache;
import by.sakujj.cache.LRUCache;
import by.sakujj.connection.ConnectionPool;
import by.sakujj.connection.ConnectionPoolImpl;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dao.ClientDAOImpl;
import by.sakujj.hashing.BCryptHasher;
import by.sakujj.hashing.Hasher;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.proxy.DynamicProxyCreator;
import by.sakujj.services.ClientService;
import by.sakujj.services.impl.ClientServiceImpl;
import by.sakujj.util.PropertiesUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * A context to get application classes instances from.
 */
@Slf4j
public class Context implements AutoCloseable {
    private final Map<Class<?>, Object> instanceContainer = new HashMap<>();

    private static final String DEFAULT_PROPERTIES_FILE_NAME = "application.yaml";
    private final String propertiesFileName;

    public Context(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
        initInstanceContainer();
    }

    public Context() {
        this.propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;
        initInstanceContainer();
    }


    @Override
    public void close() throws Exception {
        instanceContainer
                .entrySet()
                .stream()
                .filter(e -> AutoCloseable.class.isAssignableFrom(e.getKey()))
                .forEach(e -> {
                    try {
                        ((AutoCloseable) e.getValue()).close();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    /**
     * Used to get a stored instance.
     *
     * @param clazz {@link Class} of the stored instance
     * @return A stored instance if any was put or null.
     */
    public <T> T getByClass(Class<T> clazz) {
        return (T) instanceContainer.get(clazz);
    }

    /**
     * Used to put a new instance in the context. The corresponding class should be specified.
     *
     * @param clazz    class of the instance to put.
     * @param instance instance to put
     */
    public <T> void putInstanceOf(Class<T> clazz, T instance) {
        instanceContainer.put(clazz, instance);
    }


    private void initInstanceContainer() {
        putInstanceOf(Hasher.class, new BCryptHasher());
        putInstanceOf(ConnectionPool.class, new ConnectionPoolImpl(propertiesFileName));
        putCache();
        putValidators();
        putDAOs();
        putMappers();
        putServices();
    }

    private void putServices() {
        ClientService clientService = new ClientServiceImpl(
                getByClass(ClientDAO.class),
                getByClass(ClientMapper.class),
                getByClass(ConnectionPool.class));
        putInstanceOf(ClientService.class, clientService);
    }

    private void putMappers() {
        ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);
        clientMapper.setHasher(getByClass(Hasher.class));
        putInstanceOf(ClientMapper.class, clientMapper);
    }

    private void putValidators() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        putInstanceOf(Validator.class, validator);
    }

    private void putCache() {
        CacheConfig cacheConfig;
        try {
            cacheConfig = new CacheConfig(PropertiesUtil.newPropertiesFromYaml("dao.cache", propertiesFileName));
        } catch (Throwable t) {
            cacheConfig = new CacheConfig();
        }

        log.info("Cache type: " + cacheConfig.getType()+", capacity: " + cacheConfig.getCapacity());

        Cache cache = switch (cacheConfig.getType()) {
            case LRU -> new LRUCache(cacheConfig.getCapacity());
            case LFU -> new LFUCache(cacheConfig.getCapacity());
            case NONE -> null;
        };
        putInstanceOf(Cache.class, cache);
    }

    private void putDAOs() {
        Cache cache = getByClass(Cache.class);
        if (cache == null) {
            putInstanceOf(ClientDAO.class, new ClientDAOImpl());
            return;
        }

        putInstanceOf(
                ClientDAO.class,
                (ClientDAO) DynamicProxyCreator
                        .newDAOCachingProxy(new ClientDAOImpl(), cache)
        );
    }

}
