package by.sakujj.cache;

import by.sakujj.model.Client;
import org.junit.jupiter.api.Test;

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

    @Test
    public void shouldAdd() {
        // given
        int size = 3;
        LRUCache lruCache = new LRUCache(size);

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        lruCache.add(c1);
        lruCache.add(c2);
        lruCache.add(c3);
        var c1Optional = lruCache.getById(c1.getId());
        var c2Optional = lruCache.getById(c2.getId());
        var c3Optional = lruCache.getById(c3.getId());

        // then
        assertThat(lruCache.getSize()).isEqualTo(size);

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

        assertThat(c2Optional).isPresent();
        assertThat(c2Optional.get()).isEqualTo(c2);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);
    }

    @Test
    public void shouldRemoveLruWithSize1() {
        // given
        int size = 1;
        LRUCache lruCache = new LRUCache(size);

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        lruCache.add(c1);
        lruCache.add(c2);
        lruCache.add(c3);
        var c1Optional = lruCache.getById(c1.getId());
        var c2Optional = lruCache.getById(c2.getId());
        var c3Optional = lruCache.getById(c3.getId());

        // then
        assertThat(lruCache.getSize()).isEqualTo(size);

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

        assertThat(c2Optional).isEmpty();

        assertThat(c3Optional).isEmpty();
    }

    @Test
    public void shouldRemoveLruWithSize2() {
        // given
        int size = 2;
        LRUCache lruCache = new LRUCache(size);

        var c1 = testClients.get(0);
        var c2 = testClients.get(1);
        var c3 = testClients.get(2);

        // when
        lruCache.add(c1);
        lruCache.add(c2);
        var tmp = lruCache.getById(c1.getId());
        lruCache.add(c3);
        var c1Optional = lruCache.getById(c1.getId());
        var c2Optional = lruCache.getById(c2.getId());
        var c3Optional = lruCache.getById(c3.getId());

        // then
        assertThat(lruCache.getSize()).isEqualTo(size);

        assertThat(c2Optional).isEmpty();

        assertThat(c1Optional).isPresent();
        assertThat(c1Optional.get()).isEqualTo(c1);

        assertThat(c3Optional).isPresent();
        assertThat(c3Optional.get()).isEqualTo(c3);
    }
}


