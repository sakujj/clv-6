package by.sakujj.cache;

import by.sakujj.model.Entity;

import java.util.Optional;

public class NoOpCache implements Cache{
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

    @Override
    public int getSize() {
        return 0;
    }
}
