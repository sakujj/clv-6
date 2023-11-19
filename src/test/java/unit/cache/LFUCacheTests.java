package unit.cache;

import by.sakujj.cache.Cache;
import by.sakujj.cache.LFUCache;
import by.sakujj.model.Client;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LFUCacheTests {
    private final List<Client> testClients = CacheImplTests.getTestsClients();

    @Test
    public void shouldRemoveImplicitlyLfuWithSize2() {
        // given
        int size = 2;
        Cache cache = new LFUCache(size);

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        cache.addOrUpdate(c1);
        cache.addOrUpdate(c2);

        // LFU is c1, LRU is c2
        var tmp = cache.getById(c2.getId());
        tmp = cache.getById(c2.getId());
        tmp = cache.getById(c1.getId());

        cache.addOrUpdate(c3);

        var c1Optional = cache.getById(c1.getId());
        var c2Optional = cache.getById(c2.getId());
        var c3Optional = cache.getById(c3.getId());

        // then
        assertThat(cache.getSize()).isEqualTo(size);

        assertThat(c1Optional).isEmpty();

        assertThat(c2Optional).isPresent();
        assertThat(c2Optional.get()).isEqualTo(c2);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);
    }


}


