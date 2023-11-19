package unit.cache;

import by.sakujj.cache.Cache;
import by.sakujj.cache.CacheConfig;
import by.sakujj.cache.LFUCache;
import by.sakujj.cache.LRUCache;
import by.sakujj.model.Client;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LRUCacheTests {
    public final List<Client> testClients = List.of(
            Client.builder()
                    .id(UUID.fromString("f27f3c0c-b417-4a6c-90b2-cbe95083821c"))
                    .username("Ivan Ivanov Ivanovich")
                    .email("ivanov123123555@mail.ru")
                    .age(30)
                    .password("sdjfklfjxxxxxzzzzzzz")
                    .build(),
            Client.builder()
                    .id(UUID.fromString("30851dc1-c81d-4c48-a5e4-5ca0e9b5e881"))
                    .username("Andrew Huberman")
                    .email("andyhubermm@gmail.com")
                    .age(52)
                    .password("sdfaddsds")
                    .build(),
            Client.builder()
                    .id(UUID.fromString("a2691e36-acdc-41b9-a909-2d16bc72cc7c"))
                    .username("Sara Connor")
                    .email("sarrr123000@yandex.by")
                    .age(78)
                    .password("sdgfgdfcccc")
                    .build()

    );

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldUpdate(CacheConfig.CacheType cacheType) {
        // given
        int size = 3;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var updatedC1 = testClients.get(2);
        updatedC1.setId(c1.getId());

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        cache.addOrUpdate(updatedC1);
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size - 1);

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(updatedC1);

        assertThat(c2Optional).isPresent();
        assertThat(c2Optional.get()).isEqualTo(c2);

    }

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldAdd(CacheConfig.CacheType cacheType) {
        // given
        int size = 3;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        cache.addOrUpdate(c3);
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size);

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

        assertThat(c2Optional).isPresent();
        assertThat(c2Optional.get()).isEqualTo(c2);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);
    }

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldRemoveImplicitlyLruWithSize1(CacheConfig.CacheType cacheType) {
        // given
        int size = 1;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        cache.addOrUpdate(c3);
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);

        assertThat(c2Optional).isEmpty();

        assertThat(c1Optional).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldRemoveImplicitlyLruWithSize2(CacheConfig.CacheType cacheType) {
        // given
        int size = 2;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        var tmp = cache.getById(c1.getId());
        cache.addOrUpdate(c3);
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size);

        assertThat(c2Optional).isEmpty();

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);
    }

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldRemoveExplicitlyLruWithSize3(CacheConfig.CacheType cacheType) {
        // given
        int size = 3;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        cache.addOrUpdate(c3);
        cache.removeById(c2.getId());
        cache.removeById(c3.getId());
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size - 2);

        assertThat(c2Optional).isEmpty();

        assertThat(c3Optional).isEmpty();

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

    }

    @ParameterizedTest
    @MethodSource("getCacheTypes")
    public void shouldAddAfterRemoveExplicitlyLruWithSize3(CacheConfig.CacheType cacheType) {
        // given
        int size = 3;
        Cache cache = switch (cacheType) {
            case LFU -> new LFUCache(size);
            case LRU -> new LRUCache(size);
            case NONE -> null;
        };

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);
        cache.addOrUpdate(c3);
        cache.removeById(c2.getId());
        cache.removeById(c3.getId());
        cache.addOrUpdate(c3);
        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size - 1);

        assertThat(c2Optional).isEmpty();

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);
    }

    static List<CacheConfig.CacheType> getCacheTypes() {
        return List.of(
                CacheConfig.CacheType.LRU,
                CacheConfig.CacheType.LFU
        );
    }
}


