package by.sakujj.cache;

import lombok.Getter;

import java.util.Properties;

@Getter
public class CacheConfig {
    public enum CacheType{
        LRU, LFU, NONE
    }

    private static final CacheType DEFAULT_TYPE = CacheType.NONE;
    private static final int DEFAULT_CAPACITY = 0;

    private final CacheType type;
    private final Integer capacity;

    public CacheConfig() {
        type = DEFAULT_TYPE;
        capacity = DEFAULT_CAPACITY;
    }
    public CacheConfig(Properties properties) {
        type = CacheType.valueOf((String)properties.getOrDefault("type", DEFAULT_TYPE));
        capacity = (Integer) properties.getOrDefault("capacity", 0);
    }
}
