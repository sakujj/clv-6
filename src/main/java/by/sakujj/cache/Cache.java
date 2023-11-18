package by.sakujj.cache;

import by.sakujj.model.Entity;

import java.util.Optional;

public interface Cache {
    void add(Entity<?, ?> entity);

    Optional<Entity<?, ?>> getById(Object id);
    void removeById(Object id);

}
