package by.sakujj.cache;

import by.sakujj.model.Entity;

import java.util.Optional;

public class LFUCache implements Cache{
    private final int capacity;
    public LFUCache(int capacity) {
        this.capacity = capacity;
    }
    @Override
    public void addOrUpdate(Entity<?, ?> entity) {

    }

    @Override
    public Optional<Entity<?, ?>> getById(Object id) {
        return Optional.empty();
    }

    @Override
    public void removeById(Object id) {

    }
}
