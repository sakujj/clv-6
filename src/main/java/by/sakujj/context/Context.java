package by.sakujj.context;

import by.sakujj.cache.Cache;
import by.sakujj.cache.CacheConfig;
import by.sakujj.cache.LFUCache;
import by.sakujj.cache.LRUCache;
import by.sakujj.connection.ConnectionPool;
import by.sakujj.connection.ConnectionPoolImpl;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dao.ClientDAOImpl;
import by.sakujj.proxy.DynamicProxyCreator;
import by.sakujj.util.PropertiesUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A context to get application classes instances from.
 */
public class Context {
    private final Map<Class<?>, Object> instanceContainer = new HashMap<>();
    private static final String PROPERTIES_FILE_NAME = "application.yaml";

    public Context() {
        initInstanceContainer();
    }

    /**
     * Used to get a stored instance.
     * @param clazz {@link Class} of the stored instance
     * @return A stored instance if any was put or null.
     */
    public <T> T getByClass(Class<T> clazz) {
        return (T) instanceContainer.get(clazz);
    }

    /**
     * Used to put a new instance in the context. The corresponding class should be specified.
     * @param clazz class of the instance to put.
     * @param instance instance to put
     */
    public <T> void putInstanceOf(Class<T> clazz, T instance) {
        instanceContainer.put(clazz, instance);
    }

    private void initInstanceContainer() {
        putCache();
        putInstanceOf(ConnectionPool.class, new ConnectionPoolImpl(PROPERTIES_FILE_NAME));

        putValidators();
        putDAOs();
    }

    private void putValidators() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        putInstanceOf(Validator.class, validator);
    }

    private void putCache() {
        CacheConfig cacheConfig = new CacheConfig(PropertiesUtil.newPropertiesFromYaml("dao.cache", PROPERTIES_FILE_NAME));
        Cache cache = switch (cacheConfig.getType()) {
            case LRU -> new LRUCache(cacheConfig.getCapacity());
            case LFU -> new LFUCache(cacheConfig.getCapacity());
        };
        putInstanceOf(Cache.class, cache);
    }

    private void putDAOs() {
        Cache cache = getByClass(Cache.class);
        putInstanceOf(
                ClientDAO.class,
                (ClientDAO) DynamicProxyCreator
                        .newDAOCachingProxy(new ClientDAOImpl(), cache)
        );
    }


}
