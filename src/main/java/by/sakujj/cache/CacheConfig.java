package by.sakujj.cache;

import lombok.Getter;

import java.util.Properties;

@Getter
public class CacheConfig {
    public enum CacheType{
        LRU, LFU
    }

    private static final CacheType DEFAULT_TYPE = CacheType.LRU;
    private static final int DEFAULT_CAPACITY = 1000;

    private final CacheType type;
    private final int capacity;

    public CacheConfig(Properties properties) {
        type = CacheType.valueOf((String)properties.getOrDefault("type", DEFAULT_TYPE));
        capacity = (int) properties.getOrDefault("capacity", DEFAULT_CAPACITY);
    }
}
